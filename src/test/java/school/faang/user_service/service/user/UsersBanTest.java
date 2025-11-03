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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersBanListenerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Message message;

    @InjectMocks
    private UsersBanListener usersBanListener;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void onMessage_ValidUserIds_ShouldBanUsers() throws JsonProcessingException {
        List<Long> userIds = List.of(1L, 2L, 3L);
        String jsonMessage = objectMapper.writeValueAsString(userIds);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, times(1)).bannedByIds(userIds);
    }

    @Test
    void onMessage_EmptyUserList_ShouldNotBanUsers() throws JsonProcessingException {
        List<Long> emptyList = List.of();
        String jsonMessage = objectMapper.writeValueAsString(emptyList);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    void onMessage_InvalidJson_ShouldNotBanUsers() {
        String invalidJson = "invalid json";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(invalidJson.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    void onMessage_EmptyBody_ShouldNotBanUsers() {
        String emptyBody = "";
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(emptyBody.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, never()).bannedByIds(anyList());
    }

    @Test
    void onMessage_SingleUserId_ShouldBanUser() throws JsonProcessingException {
        List<Long> userIds = List.of(10L);
        String jsonMessage = objectMapper.writeValueAsString(userIds);
        byte[] pattern = "user-ban-topic".getBytes();

        when(message.getBody()).thenReturn(jsonMessage.getBytes());

        usersBanListener.onMessage(message, pattern);

        verify(userRepository, times(1)).bannedByIds(userIds);
    }
}