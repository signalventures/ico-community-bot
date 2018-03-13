package vc.signal;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility class to mock Telegram API related objects.
 */
public class MockUtils {

  /**
   * Create a mock-version of a telegram message.
   */
  public static Message mockMessage(String text, Long chatId, User fromUser) {
    Message message = mock(Message.class);
    when(message.getMessageId()).thenReturn(1);
    when(message.getText()).thenReturn(text);
    when(message.getChatId()).thenReturn(chatId);
    when(message.getFrom()).thenReturn(fromUser);
    return message;
  }

  public static User mockUser(Integer id, String username) {
    User user = mock(User.class);
    when(user.getId()).thenReturn(id);
    when(user.getUserName()).thenReturn(username);
    return user;
  }
}
