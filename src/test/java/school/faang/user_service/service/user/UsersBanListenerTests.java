package school.faang.user_service.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersBanListenerTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private Message message;

    @InjectMocks
    private UsersBanListener usersBanListener;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void onMessage_validUserIds_shouldBanUsers() throws JsonProcessingException {
        List<Long> userIds = List.of(1L, 2L, 3L);
        String jsonMessage = objectMapper.writeValueAsString(userIds);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, times(1)).bannedByIds(userIds);
    }

    @Test
    public void onMessage_emptyUserList_shouldNotBanUsers() throws JsonProcessingException {
        List<Long> emptyList = List.of();
        String jsonMessage = objectMapper.writeValueAsString(emptyList);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_invalidJson_shouldNotBanUsers() {
        String invalidJson = "invalid json";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(invalidJson.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_emptyBody_shouldNotBanUsers() {
        String emptyBody = "";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(emptyBody.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_singleUserId_shouldBanUser() throws JsonProcessingException {
        List<Long> userIds = List.of(10L);
        String jsonMessage = objectMapper.writeValueAsString(userIds);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, times(1)).bannedByIds(userIds);
    }

    @Test
    public void onMessage_nullBody_shouldNotBanUsers() {
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(new byte[0]);

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_whitespaceJson_shouldNotBanUsers() {
        String whitespaceJson = "   ";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(whitespaceJson.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_nullInJson_shouldNotBanUsers() {
        String nullJson = "null";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(nullJson.getBytes());

        assertThrows(NullPointerException.class, () -> {
            usersBanListener.onMessage(message, pattern);
        });

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    public void onMessage_generalException_shouldNotBanUsers() {
        byte[] pattern = "user-ban-topic".getBytes();
        when(message.getBody()).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> {
            usersBanListener.onMessage(message, pattern);
        });

        verify(userRepository, never()).bannedByIds(anyList());
    }
}