package vc.signal.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.services.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detects users who try to impersonate an admin and bans them from the channel.
 */
@Component
public class AdminImpersonatorBlocker implements BotPlugin {

  private static final Logger logger = LoggerFactory.getLogger(AdminImpersonatorBlocker.class);

  private CommunityBot bot;

  private UserRepository userRepository;

  public AdminImpersonatorBlocker(CommunityBot bot, UserRepository userRepository) {
    this.bot = bot;
    this.userRepository = userRepository;
  }

  @Override
  public void onNewChatMembers(Message message, List<User> newChatMembers) throws TelegramApiException {
    final String chatId = String.valueOf(message.getChatId());
    final Set<String> adminsDisplayName = userRepository.getAdmins(chatId)
        .stream()
        .map(adminId -> userRepository.getUser(adminId))
        .map(userFields -> userFields.get("firstName") + userFields.get("lastName"))
        .collect(Collectors.toSet());

    for (User newChatMember : newChatMembers) {
      String newChatMemberDisplayName = newChatMember.getFirstName() + newChatMember.getLastName();
      if (adminsDisplayName.contains(newChatMemberDisplayName)) {
        logger.warn("Found an impersonator of {} with username {}.", newChatMemberDisplayName, newChatMember.getUserName());
        bot.execute(new KickChatMember(chatId, newChatMember.getId()).setUntilDate(-1));
      }
    }
  }
}
