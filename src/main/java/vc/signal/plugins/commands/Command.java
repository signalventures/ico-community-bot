package vc.signal.plugins.commands;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Base interface for all commands.
 */
public interface Command {

  /**
   * Executes a command based on the received <code>message</code>.
   */
  void execute(Message message) throws TelegramApiException;

  /**
   * Gets the command name (e.g. mute)
   */
  String getName();
}
