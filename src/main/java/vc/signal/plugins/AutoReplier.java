package vc.signal.plugins;

import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;

import static vc.signal.utils.I18n.t;

/**
 * Plugin to automatically replying to messages containing certain keywords.
 */
@Component
public class AutoReplier implements BotPlugin {

  private static final Logger logger = LoggerFactory.getLogger(AutoReplier.class);

  private CommunityBot bot;

  public AutoReplier(CommunityBot bot) {
    this.bot = bot;
  }

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
    String messageText = message.getText();
    if (messageText.toLowerCase().contains("when moon")) {
      // TODO: Store pairs of keyword=reply and allow admin to set them...
      bot.execute(new SendMessage(message.getChatId(),
          EmojiParser.parseToUnicode(t(":new_moon::waxing_crescent_moon::first_quarter_moon::waxing_gibbous_moon::full_moon::rocket:")))
        .setReplyToMessageId(message.getMessageId()));
    }
  }
}