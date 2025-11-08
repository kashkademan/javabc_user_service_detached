package school.faang.user_service.messages.redis.listeners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import school.faang.user_service.config.redis.GenericJacksonConfig;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersBanListenerTest {
    @Mock
    private UserService userService;

    @Mock
    private GenericJacksonConfig genericJacksonConfig;

    @Mock
    private GenericJackson2JsonRedisSerializer genericJackson;

    @Mock
    private Message message;

    private UsersBanListener usersBanListener;

    @BeforeEach
    void setUp() {
        usersBanListener = new UsersBanListener(userService, genericJacksonConfig);
        when(genericJacksonConfig.getGenericJackson()).thenReturn(genericJackson);
    }

    @Test
    void testOnMessage_whenValidMessageReceived_shouldCallBanUsers() {
        byte[] messageBody = new byte[]{};
        byte[] pattern = "user-ban-topic".getBytes();
        List<Long> expectedUserIds = List.of(1L, 2L, 3L);

        when(message.getBody()).thenReturn(messageBody);
        when(genericJackson.deserialize(messageBody)).thenReturn(expectedUserIds);

        usersBanListener.onMessage(message, pattern);

        verify(userService).banUsers(expectedUserIds);
    }

    @Test
    void testOnMessage_whenEmptyListReceived_shouldCallBanUsersWithEmptyList() {
        byte[] messageBody = new byte[]{};
        byte[] pattern = "user-ban-topic".getBytes();
        List<Long> expectedUserIds = List.of();

        when(message.getBody()).thenReturn(messageBody);
        when(genericJackson.deserialize(messageBody)).thenReturn(expectedUserIds);

        usersBanListener.onMessage(message, pattern);

        verify(userService).banUsers(expectedUserIds);
    }
}
