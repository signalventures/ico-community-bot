package vc.signal.plugins;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.config.ApplicationConfig;
import vc.signal.services.ChatRepository;
import vc.signal.services.UserRepository;
import vc.signal.utils.BlockchainAddressMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static vc.signal.utils.I18n.t;

/**
 * Plugin to filter different types of media from a channel.
 */
@Component
public class MediaFilter implements BotPlugin {

  private CommunityBot bot;
  private ChatRepository chatRepository;
  private UserRepository userRepository;

  public MediaFilter(CommunityBot bot, ChatRepository chatRepository, UserRepository userRepository) {
    this.bot = bot;
    this.chatRepository = chatRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
    User fromUser = message.getFrom();
    String userId = String.valueOf(fromUser.getId());
    Long chatId = message.getChatId();
    if (userRepository.isAdmin(String.valueOf(chatId), userId)) return;

    boolean hasUrl = Optional.ofNullable(message.getEntities())
        .orElse(Collections.emptyList())
        .stream()
        .anyMatch(messageEntity -> messageEntity.getType().equals("url"));
    if (hasUrl) {
      int maxWarnings = chatRepository.getMaxWarnings(chatId);
      int userWarnings = chatRepository.warnUser(chatId, userId);

      bot.execute(new DeleteMessage(chatId, message.getMessageId()));
      bot.execute(new SendMessage(chatId,
          t("Links are not allowed in this channel. %s <b>has been warned</b> (<code>%d/%d</code>)", fromUser.getUserName(), userWarnings, maxWarnings))
          .enableHtml(true));
      if (userWarnings == maxWarnings) {
        bot.execute(new KickChatMember(chatId, fromUser.getId()).setUntilDate(-1));
        chatRepository.removeUserWarnings(chatId, userId);
      }
    }
  }
}