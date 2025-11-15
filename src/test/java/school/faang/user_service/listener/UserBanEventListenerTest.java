package school.faang.user_service.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.event.UserBanEvent;
import school.faang.user_service.messaging.listener.UserBanEventListener;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserBanEventListenerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserBanEventListener userBanEventListener;

    @Test
    void testOnMessagePositive() {
        List<Long> usersIds = List.of(1L);
        UserBanEvent userBanEvent = UserBanEvent.builder()
                .userIds(usersIds)
                .build();

        userBanEventListener.onMessage(userBanEvent);

        Mockito.verify(userService).banUsers(Mockito.eq(usersIds));
    }
}