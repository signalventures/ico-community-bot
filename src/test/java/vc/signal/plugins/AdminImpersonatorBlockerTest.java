package vc.signal.plugins;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.telegram.telegrambots.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import vc.signal.CommunityBot;
import vc.signal.services.UserRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static vc.signal.MockUtils.mockMessage;
import static vc.signal.MockUtils.mockUser;

/**
 * @see AdminImpersonatorBlocker
 */
public class AdminImpersonatorBlockerTest {

  private static final Long CHAT_ID = -1L;

  private static final User ADMIN = mockUser(1, "vitalik");

  private CommunityBot bot;
  private UserRepository userRepository;
  private AdminImpersonatorBlocker plugin;

  @BeforeClass
  public static void beforeClass() {
    when(ADMIN.getFirstName()).thenReturn("Vitalik");
  }

  @Before
  public void before() {
    bot = mock(CommunityBot.class);
    userRepository = mock(UserRepository.class);
    when(userRepository.getAdmins(String.valueOf(CHAT_ID))).thenReturn(Sets.newHashSet("1"));

    Map<String, String> adminFields = new HashMap<>();
    adminFields.put("firstName", "Vitalik");
    when(userRepository.getUser("1")).thenReturn(adminFields);

    plugin = spy(new AdminImpersonatorBlocker(bot, userRepository));
  }

  @Test
  public void testAdminImpersonatorIsKickedFromChannel() throws TelegramApiException {
    User impersonator = mockUser(2, "vitalyk");
    when(impersonator.getFirstName()).thenReturn("Vitalik");
    Message message = mockMessage(null, CHAT_ID, impersonator);
    plugin.onNewChatMembers(message, Arrays.asList(impersonator));
    verify(bot).execute(any(KickChatMember.class));
  }

  @Test
  public void testRegularUserIsNotKickedFromChannel() throws TelegramApiException {
    User impersonator = mockUser(2, "John");
    when(impersonator.getFirstName()).thenReturn("McAffee");
    Message message = mockMessage(null, CHAT_ID, impersonator);
    plugin.onNewChatMembers(message, Arrays.asList(impersonator));
    verify(bot, never()).execute(any());
  }
}
