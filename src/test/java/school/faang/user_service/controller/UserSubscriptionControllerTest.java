package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.controller.user.UserSubscriptionController;
import school.faang.user_service.service.user.UserSubscriptionServiceImpl;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSubscriptionController.class)
public class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSubscriptionServiceImpl subscriptionService;

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

        when(userContext.getUserId()).thenReturn(followerId);

        mockMvc.perform(MockMvcRequestBuilders.post("/followers/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Проверка успешной отписки через контроллер")
    void shouldUnfollowSuccessfully() throws Exception {
        long followerId = 1L;

        when(userContext.getUserId()).thenReturn(followerId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/followers/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение количества подписчиков через контроллер")
    void testGetFollowersCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/followers/2/followers-count"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение количества подписок пользователя через контроллер")
    void testGetFolloweesCount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/followers/2/followees-count"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение всех подписчиков пользователя через контроллер")
    void testGetFollowers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/followers/2/followers"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение всех подписок пользователя через контроллер")
    void testGetFollowees() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/followers/2/followees"))
                .andExpect(status().isOk());
    }
}
