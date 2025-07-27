package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.user.UserSubscriptionService;

@ExtendWith(MockitoExtension.class)
public class UserSubscriptionControllerTest {

    @Mock
    private UserSubscriptionService userSubscriptionService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserSubscriptionController userSubscriptionController;

    @Test
    void followUser() {

    }

}
