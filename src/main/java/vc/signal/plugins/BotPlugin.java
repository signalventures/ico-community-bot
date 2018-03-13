package vc.signal.plugins;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

/**
 * Bot plugin contract providing implementations for different Telegram events.
 */
public interface BotPlugin {

  default void onEditedMessage(Message message) throws TelegramApiException {
    onTextMessage(message);
  }

  default void onTextMessage(Message message) throws TelegramApiException {}

  default void onNewChatMembers(Message message, List<User> newChatMembers) throws TelegramApiException {}

  default void onCallbackQuery(Message message) {}

}
