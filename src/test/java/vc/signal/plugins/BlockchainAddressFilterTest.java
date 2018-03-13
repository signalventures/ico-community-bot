package vc.signal.plugins;

import org.junit.Before;
import org.junit.Test;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.services.ChatRepository;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static vc.signal.MockUtils.mockMessage;
import static vc.signal.MockUtils.mockUser;

/**
 * @see BlockchainAddressFilter
 */
public class BlockchainAddressFilterTest {

  private static final Long CHAT_ID = -1L;
  private static final String WHITELISTED_ADDRESS = "0x0000000000000000000000000000000000000000";

  private CommunityBot bot;
  private ChatRepository chatRepository;
  private BlockchainAddressFilter plugin;

  @Before
  public void before() {
    bot = mock(CommunityBot.class);

    chatRepository = mock(ChatRepository.class);
    when(chatRepository.getMaxWarnings(CHAT_ID)).thenReturn(3);

    plugin = spy(new BlockchainAddressFilter(bot, chatRepository));
    doReturn(Arrays.asList(WHITELISTED_ADDRESS)).when(plugin).getWhitelistedAddresses();
  }

  @Test
  public void testMessageIsDeletedAndUserIsWarnedOnNonWhitelistedAddress() throws TelegramApiException {
    User user = mockUser(1, "vitalik");
    Message message = mockMessage("Please send all your ether to 0xab5801a7d398351b8be11c439e05c5b3259aec9b.", CHAT_ID, user);
    plugin.onTextMessage(message);
    // Delete user message.
    verify(bot).execute(any(DeleteMessage.class));
    // Send warning message to the user.
    verify(bot).execute(any(SendMessage.class));
  }

  @Test
  public void testUserIsKickedAfterXWarnings() throws TelegramApiException {
    User user = mockUser(1, "vitalik");
    Message message = mockMessage("Please send all your ether to 0xab5801a7d398351b8be11c439e05c5b3259aec9b.", CHAT_ID, user);
    when(chatRepository.warnUser(CHAT_ID, String.valueOf(user.getId()))).thenReturn(3);
    plugin.onTextMessage(message);
    // User is kicked out of the channel.
    verify(bot).execute(any(KickChatMember.class));
    // Warnings are cleared from the repository.
    verify(chatRepository).removeUserWarnings(CHAT_ID, String.valueOf(user.getId()));
  }

  @Test
  public void testNothingHappensWhenUserEntersWhitelistedAddress() throws TelegramApiException {
    User user = mockUser(1, "vitalik");
    Message message = mockMessage("Please send all your ether to 0x0000000000000000000000000000000000000000.", CHAT_ID, user);
    plugin.onTextMessage(message);
    // Nothing happens, bot just ignores the message.
    verify(bot, never()).execute(any());
  }
}
