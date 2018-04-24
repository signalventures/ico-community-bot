package vc.signal.plugins;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.services.ChatRepository;
import vc.signal.services.UserRepository;

import java.util.List;

/**
 * TODO: Collect statistics on messages, new chat members, etc.
 * To be decided if we need something custom made, since there already exists combot for this (https://combot.org).
 */
@Component
public class StatsCollector implements BotPlugin {

  private CommunityBot bot;
  private ChatRepository chatRepository;

  public StatsCollector(CommunityBot bot, ChatRepository chatRepository) {
    this.bot = bot;
    this.chatRepository = chatRepository;
  }

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
  }

  @Override
  public void onNewChatMembers(Message message, List<User> newChatMembers) {
  }
}
