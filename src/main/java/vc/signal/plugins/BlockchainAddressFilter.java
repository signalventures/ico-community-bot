package vc.signal.plugins;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.config.ApplicationConfig;
import vc.signal.services.ChatRepository;
import vc.signal.utils.BlockchainAddressMatcher;

import java.util.List;

import static vc.signal.utils.I18n.t;

/**
 * Plugin to filter blockchain addresses.
 */
@Component
public class BlockchainAddressFilter implements BotPlugin {

  private static final String WHITELISTED_ADDRESSES_PROPERTY = "config.plugins.blockchainaddressfilter.whitelisted-addresses";

  private CommunityBot bot;
  private ChatRepository chatRepository;

  public BlockchainAddressFilter(CommunityBot bot, ChatRepository chatRepository) {
    this.bot = bot;
    this.chatRepository = chatRepository;
  }

  @Override
  public void onTextMessage(Message message) throws TelegramApiException {
    String messageText = message.getText();
    final List<String> whitelistedAddresses = getWhitelistedAddresses();
    List<String> matchedAddresses = BlockchainAddressMatcher.match(messageText);
    if (matchedAddresses.stream().anyMatch(addr -> !whitelistedAddresses.contains(addr))) {
      User fromUser = message.getFrom();
      String userId = String.valueOf(fromUser.getId());
      Long chatId = message.getChatId();
      int maxWarnings = chatRepository.getMaxWarnings(chatId);
      int userWarnings = chatRepository.warnUser(chatId, userId);

      bot.execute(new DeleteMessage(chatId, message.getMessageId()));
      bot.execute(new SendMessage(chatId,
          t("%s <b>has been warned</b> (<code>%d/%d</code>)", fromUser.getUserName(), userWarnings, maxWarnings))
          .enableHtml(true));

      if (userWarnings == maxWarnings) {
        bot.execute(new KickChatMember(chatId, fromUser.getId()).setUntilDate(-1));
        // bot.execute(new RestrictChatMember(message.getChatId(), message.getFrom().getId()));
        chatRepository.removeUserWarnings(chatId, userId);
      }
    }
  }

  public List<String> getWhitelistedAddresses() {
    return ApplicationConfig.INSTANCE.getPropertyAsList(WHITELISTED_ADDRESSES_PROPERTY);
  }
}