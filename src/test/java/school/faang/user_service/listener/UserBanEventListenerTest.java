package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.event.UserBanEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.messaging.listener.UserBanEventListener;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserBanEventListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    private UserBanEventListener userBanEventListener;

    @BeforeEach
    void setup() {
        userBanEventListener = new UserBanEventListener(objectMapper, userService);
    }

    @Test
    void testOnMessageThrowsExceptionIfCantDeserializeEvent() {
        String event = "test";
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
                userBanEventListener.onMessage(event));

        assertEquals("Failed to deserialize event %s".formatted(event), dataValidationException.getMessage());
    }

    @Test
    void testOnMessagePositive() throws JsonProcessingException {
        List<Long> usersIds = List.of(1L);
        UserBanEvent userBanEvent = UserBanEvent.builder()
                .userIds(usersIds)
                .build();

        userBanEventListener.onMessage(objectMapper.writeValueAsString(userBanEvent));

        Mockito.verify(userService).banUsers(Mockito.eq(usersIds));
    }
}