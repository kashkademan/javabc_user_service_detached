package school.faang.user_service.messages.redis.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
    public void onMessage_WhenNonEmptyList_ShouldBanUsers() {
        List<Long> usersIdList = Arrays.asList(1L, 2L, 3L);
        byte[] messageBody = serializeToJson(usersIdList);
        when(message.getBody()).thenReturn(messageBody);

        usersBanListener.onMessage(message, "user_ban_topic".getBytes());

        verify(userRepository, times(1)).bannedByIds(usersIdList);
        verify(userRepository, never()).bannedByIds(List.of());
    }

    @Test
    public void onMessage_WhenEmptyList_ShouldNotCallUserRepository() {
        List<Long> emptyList = Collections.emptyList();
        byte[] messageBody = serializeToJson(emptyList);
        when(message.getBody()).thenReturn(messageBody);

        usersBanListener.onMessage(message, "user_ban_topic".getBytes());

        verify(userRepository, never()).bannedByIds(any());
    }

    private byte[] serializeToJson(List<Long> list) {
        return new org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer()
                .serialize(list);
    }
}
