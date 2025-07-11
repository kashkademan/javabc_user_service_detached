package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.controller.user.UserSubscriptionController;
import school.faang.user_service.service.user.UserSubscriptionService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSubscriptionController.class)
public class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSubscriptionService subscriptionService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @InjectMocks
    private UserSubscriptionController controller;

    @Test
    @DisplayName("Проверка успешной подписки через контроллер")
    void shouldFollowSuccessfully() throws Exception {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);

        mockMvc.perform(MockMvcRequestBuilders.post("/followers/2"))
                .andExpect(status().isOk());

        //verify(subscriptionService).followUser(followerId, followeeId);
    }
}
