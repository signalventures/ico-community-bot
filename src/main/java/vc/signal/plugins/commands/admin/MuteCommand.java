package vc.signal.plugins.commands.admin;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.plugins.commands.Command;
import vc.signal.services.ChatRepository;
import vc.signal.services.UserRepository;

import static vc.signal.utils.I18n.t;

/**
 * Mutes all messages in a channel.
 */
@Component
public class MuteCommand implements Command {

  private CommunityBot bot;
  private UserRepository userRepository;
  private ChatRepository chatRepository;

  public MuteCommand(CommunityBot bot, UserRepository userRepository, ChatRepository chatRepository) {
    this.bot = bot;
    this.userRepository = userRepository;
    this.chatRepository = chatRepository;
  }

  @Override
  public void execute(Message message) throws TelegramApiException {
    User fromUser = message.getFrom();
    String userId = String.valueOf(fromUser.getId());
    Long chatId = message.getChatId();
    if (userRepository.isAdmin(String.valueOf(chatId), userId)) {
     chatRepository.muteChat(chatId);
     bot.execute(new SendMessage(chatId, t("Channel is now muted.")));
    }
  }

  @Override
  public String getName() {
    return "mute";
  }
}
