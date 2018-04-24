package vc.signal.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.services.UserRepository;

import java.util.List;

import static vc.signal.utils.I18n.t;

/**
 * Provides a welcome message to every new chat member.
 */
@Component
public class WelcomePlugin implements BotPlugin {

  private static final Logger logger = LoggerFactory.getLogger(WelcomePlugin.class);

  private CommunityBot bot;

  private UserRepository userRepository;

  public WelcomePlugin(CommunityBot bot, UserRepository userRepository) {
    this.bot = bot;
    this.userRepository = userRepository;
  }

  @Override
  public void onNewChatMembers(Message message, List<User> newChatMembers) throws TelegramApiException {
    final String chatId = String.valueOf(message.getChatId());
    for (User newChatMember : newChatMembers) {
      bot.execute(new SendMessage(chatId,
          t("Welcome to the channel %s! Please read the pinned message first.",
              newChatMember.getFirstName()))
          .enableHtml(true)
          .disableWebPagePreview());
    }
  }
}
