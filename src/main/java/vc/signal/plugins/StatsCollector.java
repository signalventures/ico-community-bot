package vc.signal.plugins;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

/**
 * TODO: Collect statistics on messages, new chat members, etc.
 * I'm not sure if we need something custom made, since there already exists combot for this (https://combot.org).
 */
@Component
public class StatsCollector implements BotPlugin {

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
  }

  @Override
  public void onNewChatMembers(Message message, List<User> newChatMembers) {
  }
}
