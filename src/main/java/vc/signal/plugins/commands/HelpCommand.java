package vc.signal.plugins.commands;

import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;

import static vc.signal.utils.I18n.t;

/**
 * Provides help on how to use the bot (user commands, admin commands are not exposed).
 */
@Component
public class HelpCommand implements Command {

  private CommunityBot bot;

  public HelpCommand(CommunityBot bot) {
    this.bot = bot;
  }

  @Override
  public void execute(Message message) throws TelegramApiException {
    bot.execute(new SendMessage(message.getChatId(),
        EmojiParser.parseToUnicode(t("Help will arrive soon.")))
        .setReplyToMessageId(message.getMessageId()));
  }

  @Override
  public String getName() {
    return "help";
  }
}
