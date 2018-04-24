package vc.signal.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.plugins.commands.Command;

import java.util.List;
import java.util.Optional;

/**
 * Command manager plugin.
 */
@Component
public class CommandPlugin implements BotPlugin {

  private static final Logger logger = LoggerFactory.getLogger(CommandPlugin.class);

  private List<Command> commands;

  private CommunityBot bot;

  public CommandPlugin(CommunityBot bot) {
    this.bot = bot;
  }

  @Override
  public void onCommand(Message message) throws TelegramApiException {
    String commandText = removeUsernameFromCommand(message.getText().substring(1));
    Optional<Command> optionalCommand = commands.stream()
        .filter(command -> command.getName().contains(commandText))
        .findFirst();
    if (optionalCommand.isPresent()) {
      optionalCommand.get().execute(message);
    }
  }

  /**
   * In a channel, bot commands are suffixed with the bot username (e.g. /help@bot),
   * so we filter that out in here.
   */
  private String removeUsernameFromCommand(String commandText) {
    return commandText.replace("@" + bot.getBotUsername(), "");
  }

  @Autowired
  public void setCommands(List<Command> commands) {
    this.commands = commands;
    logger.info("Registered commands: {}.", commands);
  }
}
