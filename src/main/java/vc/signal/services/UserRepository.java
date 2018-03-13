package vc.signal.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import vc.signal.CommunityBot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Repository for user related information.
 */
@Repository
public class UserRepository {

  private static final String CACHE_CHAT_ADMINS = "cache:chat:%s:admins";
  private static final String CACHE_CHAT_CREATOR = "cache:chat:%s:creator";
  private static final String USER_HASH = "user:%s";

  @Autowired
  private JedisPool jedisPool;

  @Autowired
  private CommunityBot bot;

  /**
   * @returns a user object for the given <code>userId</code>.
   */
  public Map<String, String> getUser(String userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String userHashKey = String.format(USER_HASH, userId);
      return jedis.hgetAll(userHashKey);
    }
  }

  /**
   * @return set of admins (ids) for <code>chatId</code>.
   */
  public Set<String> getAdmins(String chatId) {
    final String adminSetKey = String.format(CACHE_CHAT_ADMINS, chatId);
    try (Jedis jedis = jedisPool.getResource()) {
      Set<String> adminSet = jedis.smembers(adminSetKey);
      if (adminSet.isEmpty()) {
        adminSet = cacheChatAdministrators(chatId);
      }
      return adminSet;
    }
  }

  /**
   * Checks if <code>userId</code> is an admin for <code>chatId</code>.
   */
  public boolean isAdmin(String chatId, String userId) {
    return getAdmins(chatId).contains(userId);
  }

  /**
   * Cache chat administrators since fetching them requires a REST call.
   */
  private Set<String> cacheChatAdministrators(String chatId) {
    GetChatAdministrators getChatAdministrators = new GetChatAdministrators().setChatId(chatId);
    Set<String> adminSet = new HashSet<>();
    List<ChatMember> adminList = null;
    try {
      adminList = bot.execute(getChatAdministrators);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
    final String adminSetHash = String.format(CACHE_CHAT_ADMINS, chatId);
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.del(adminSetHash);
      for (ChatMember admin : adminList) {
        String adminUserId = String.valueOf(admin.getUser().getId());
        if ("creator".equals(admin.getStatus())) {
          jedis.set(String.format(CACHE_CHAT_CREATOR, chatId), adminUserId);
        }
        saveUser(admin.getUser());
        jedis.sadd(adminSetHash, adminUserId);
        adminSet.add(adminUserId);
      }
    }
    return adminSet;
  }

  /**
   * Save user information.
   */
  public void saveUser(User user) {
    try (Jedis jedis = jedisPool.getResource()) {
      Map<String, String> userFieldValues = new HashMap<>();
      userFieldValues.put("userName", user.getUserName());
      userFieldValues.put("firstName", user.getFirstName());
      if (StringUtils.isNotEmpty(user.getLastName())) {
        userFieldValues.put("lastName", user.getLastName());
      }
      jedis.hmset(String.format(USER_HASH, String.valueOf(user.getId())), userFieldValues);
    }
  }
}
