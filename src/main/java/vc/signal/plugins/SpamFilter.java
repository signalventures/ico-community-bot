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
import vc.signal.services.ChatRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static vc.signal.utils.I18n.t;

/**
 * Plugin to filter spam on the channel.
 */
@Component
public class SpamFilter implements BotPlugin {

  private CommunityBot bot;

  private ChatRepository chatRepository;

  public SpamFilter(CommunityBot bot, ChatRepository chatRepository) {
    this.bot = bot;
    this.chatRepository = chatRepository;
  }

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
    User fromUser = message.getFrom();
    String userId = String.valueOf(fromUser.getId());
    Long chatId = message.getChatId();

    if (chatRepository.isChatMuted(chatId)) {
      bot.execute(new DeleteMessage(chatId, message.getMessageId()));
      // bot.execute(new RestrictChatMember(chatId, fromUser.getId()).setCanSendMessages(true));
      // TODO: store restricted users...
    } else {
      int spamCount = chatRepository.getSpamCount(chatId, userId) + 1;
      int maxFloodMessages = chatRepository.getMaxFloodMessages(chatId);
      chatRepository.setSpamCount(chatId, userId, spamCount);
      if (spamCount > maxFloodMessages) {
        long untilDate = Timestamp.valueOf(LocalDateTime.now().plusDays(1)).getTime();
        // TODO: library should receive a long here for unix timestamp, not an int, a PR needs to be submitted
        bot.execute(new KickChatMember(chatId, fromUser.getId()).setUntilDate((int) untilDate));
        bot.execute(new SendMessage(chatId,
            t("%s <b>kicked</b> for flood!", fromUser.getUserName()))
            .enableHtml(true));
        chatRepository.removeUserWarnings(chatId, userId);
      }
    }
  }
}