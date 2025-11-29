package school.faang.user_service.messages.redis.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersBanListenerTest {

    @Mock
    private UserService userService;
    @Mock
    private Message message;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UsersBanListener listener;

    @BeforeEach
    void init() {
        listener = new UsersBanListener(userService, objectMapper);
    }

    @Test
    void whenValidJson_thenBanUsersCalled() throws Exception {
        byte[] body = objectMapper.writeValueAsBytes(List.of(1L, 2L, 3L));
        when(message.getBody()).thenReturn(body);

        listener.onMessage(message, "user-ban".getBytes());

        verify(userService).banUsers(argThat(list ->
                list != null &&
                        list.size() == 3 &&
                        list.containsAll(List.of(1, 2, 3))
        ));
    }

    @Test
    void whenEmptyList_thenCalledWithEmptyList() throws Exception {
        byte[] body = objectMapper.writeValueAsBytes(List.of());
        when(message.getBody()).thenReturn(body);

        listener.onMessage(message, "pattern".getBytes());

        verify(userService).banUsers(List.of());
    }
}
