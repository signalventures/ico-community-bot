package vc.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.plugins.BotPlugin;
import vc.signal.utils.ThrowingConsumer;

import java.util.List;
import java.util.Optional;

/**
 * Telegram bot to monitor the channel of an ICO community.
 */
@Component
public class CommunityBot extends TelegramLongPollingBot {

  private static final Logger logger = LoggerFactory.getLogger(CommunityBot.class);

  @Value("${BOT_USERNAME}")
  private String botUsername;

  @Value("${BOT_TOKEN}")
  private String botToken;

  private List<BotPlugin> plugins;

  public void onUpdateReceived(Update update) {
    if (update.hasMessage() || update.hasEditedMessage()) {
      Message message = Optional.ofNullable(update.getMessage()).orElse(update.getEditedMessage());
      logger.info(message.toString());
      if (message.hasText()) {
        forEachPlugin(plugin -> plugin.onTextMessage(message));
      } else if (message.hasPhoto()) {
        // Ignore for now
      } else if (message.hasDocument()) {
        // Ignore for now
      } else if (message.getLeftChatMember() != null) {
        // Ignore for now
      } else if (message.getNewChatMembers() != null && !message.getNewChatMembers().isEmpty()) {
        forEachPlugin(plugin -> plugin.onNewChatMembers(message, message.getNewChatMembers()));
      }
    }
  }

  /**
   * Publish message event to each plugin.
   */
  private void forEachPlugin(ThrowingConsumer<BotPlugin, TelegramApiException> consumer) {
    plugins.stream().forEach(plugin -> {
      try {
        consumer.accept(plugin);
      } catch (TelegramApiException e) {
        logger.error("Unable to consume message for plugin {}", plugin, e);
      }
    });
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Autowired
  public void setPlugins(List<BotPlugin> plugins) {
    this.plugins = plugins;
    logger.info("Registered plugins: {}.", plugins);
  }
}
