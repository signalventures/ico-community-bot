package vc.signal.services;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Repository for chat related information (e.g. chat settings).
 */
@Repository
public class ChatRepository {

  private static final String CHAT_WARNS_HASH = "chat:%s:warns";
  private static final String CHAT_WARNSETTINGS_HASH = "chat:%s:warnsettings";

  private static final String CHAT_SPAM_WARNS_HASH = "chat:%s:spamwarns";
  private static final String CHAT_ANTISPAM_HASH = "chat:%s:antispam";

  private static final String CHAT_FLOOD_HASH = "chat:%s:flood";
  private static final String CHAT_SPAM_KEY = "cache:spam:%s:%s"; // cache:spam:chatId:userId

  private static final String CHAT_NEW_MEMBERS_KEY = "cache:newmembers:%s";

  private static final String CHAT_MUTE_KEY = "chat:%s:muted";

  @Autowired
  private JedisPool jedisPool;

  /**
   * @return max allowed warnings for the chat before an action is taken (e.g. kick or ban the user).
   */
  public int getMaxWarnings(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      String maxWarnings = jedis.hget(String.format(CHAT_WARNSETTINGS_HASH, chatId), "max");
      return NumberUtils.toInt(maxWarnings, 3);
    }
  }

  /**
   * Increments the number of warnings for a given <code>userId</code> on the given <code>chatId</code>.
   */
  public int warnUser(Long chatId, String userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      return Math.toIntExact(jedis.hincrBy(String.format(CHAT_WARNS_HASH, chatId), userId, 1));
    }
  }

  /**
   * Remove warnings for a given <code>userId</code> on the given <code>chatId</code>.
   */
  public void removeUserWarnings(Long chatId, String userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.hdel(String.format(CHAT_WARNS_HASH, chatId), userId);
    }
  }

  /**
   * @return max allowed spam warnings for the chat before an action is taken (e.g. kick or ban the user).
   */
  public int getMaxSpamWarnings(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      String maxWarnings = jedis.hget(String.format(CHAT_SPAM_WARNS_HASH, chatId), "max");
      return NumberUtils.toInt(maxWarnings, 3);
    }
  }

  /**
   * Increments the number of spam warnings for a given <code>userId</code> on the given <code>chatId</code>.
   */
  public int warnUserSpam(Long chatId, String userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      return Math.toIntExact(jedis.hincrBy(String.format(CHAT_ANTISPAM_HASH, chatId), userId, 1));
    }
  }

  /**
   * @return max allowed flood messages for the chat before an action is taken (e.g. kick or ban the user).
   */
  public int getMaxFloodMessages(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      String maxWarnings = jedis.hget(String.format(CHAT_FLOOD_HASH, chatId), "max");
      return NumberUtils.toInt(maxWarnings, 5);
    }
  }

  /**
   * @return number of spam messages for a given <code>userId</code> on the given <code>chatId</code>.
   */
  public int getSpamCount(Long chatId, String userId) {
    try (Jedis jedis = jedisPool.getResource()) {
      return NumberUtils.toInt(jedis.get(String.format(CHAT_SPAM_KEY, chatId, userId)), 0);
    }
  }

  /**
   * Increments the number of spam messages for a given <code>userId</code> on the given <code>chatId</code>.
   */
  public void setSpamCount(Long chatId, String userId, int count) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String chatSpamKey = String.format(CHAT_SPAM_KEY, chatId, userId);
      final int expireTimeInSeconds = 5;
      jedis.setex(chatSpamKey, expireTimeInSeconds, String.valueOf(count));
    }
  }

  /**
   * Mutes the channel with the given <code>chatId</code>.
   */
  public void muteChat(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String chatMuteKey = String.format(CHAT_MUTE_KEY, chatId);
      jedis.set(chatMuteKey, Boolean.TRUE.toString());
    }
  }

  /**
   * Unmutes the channel with the given <code>chatId</code>.
   */
  public void unmuteChat(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String chatMuteKey = String.format(CHAT_MUTE_KEY, chatId);
      jedis.set(chatMuteKey, Boolean.FALSE.toString());
    }
  }

  /**
   * Determines if the channel with the given <code>chatId</code> is muted.
   */
  public boolean isChatMuted(Long chatId) {
    try (Jedis jedis = jedisPool.getResource()) {
      final String chatMuteKey = String.format(CHAT_MUTE_KEY, chatId);
      return Boolean.valueOf(jedis.get(chatMuteKey));
    }
  }
}