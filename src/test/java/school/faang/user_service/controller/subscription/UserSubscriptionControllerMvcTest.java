package school.faang.user_service.controller.subscription;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.event.FollowerEvent;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.service.subscription.UserSubscriptionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSubscriptionController.class)
public class UserSubscriptionControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSubscriptionService subscriptionService;

    @MockBean
    private FollowerEventPublisher eventPublisher;

    @MockBean
    private UserContext userContext;

    @Test
    void followUserShouldCallDependenciesAndReturn204() throws Exception {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);

        mockMvc.perform(post("/api/users/{userId}/followers", followeeId))
                .andExpect(status().isNoContent());

        verify(subscriptionService, times(1)).followUser(followerId, followeeId);

        ArgumentCaptor<FollowerEvent> followerEventCaptor = ArgumentCaptor.forClass(FollowerEvent.class);
        verify(eventPublisher, times(1)).publish(followerEventCaptor.capture());

        FollowerEvent capturedFollowerEvent = followerEventCaptor.getValue();

        assertEquals(followerId, capturedFollowerEvent.followerId());
        assertEquals(followeeId, capturedFollowerEvent.followeeId());
        assertNotNull(capturedFollowerEvent.timestamp());
    }

    @Test
    void followUserShouldReturn400_whenFolloweeIdInvalid() throws Exception {
        long invalidFolloweeId = -1L;

        mockMvc.perform(post("/api/users/{userId}/followers", invalidFolloweeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void followUserShouldReturn403_whenFolloweeAndFollowerTheSameUser() throws Exception {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(post("/api/users/{userId}/followers", userId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void unfollowUserShouldCallServiceAndReturn204() throws Exception {
        long followerId = 1L;
        long followeeId = 2L;

        when(userContext.getUserId()).thenReturn(followerId);

        mockMvc.perform(delete("/api/users/{userId}/followers", followeeId))
                .andExpect(status().isNoContent());

        verify(subscriptionService, times(1)).unfollowUser(followerId, followeeId);
    }

    @Test
    void unfollowUserShouldReturn400_whenFolloweeIdInvalid() throws Exception {
        long invalidFolloweeId = -1L;

        mockMvc.perform(delete("/api/users/{userId}/followers", invalidFolloweeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void unfollowUserShouldReturn403_whenFolloweeAndFollowerTheSameUser() throws Exception {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/api/users/{userId}/followers", userId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFollowersCountShouldReturnFollowersCountAnd200() throws Exception {
        long followeeId = 1L;
        CountResponse followersCount = new CountResponse(100L);

        when(subscriptionService.getFollowersCount(followeeId))
                .thenReturn(followersCount);

        mockMvc.perform(get("/api/users/{userId}/followers/count", followeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(followersCount.count()));
    }

    @Test
    void getFollowersCountShouldReturn400_whenFolloweeIdInvalid() throws Exception {
        long invalidFolloweeId = -1L;

        mockMvc.perform(get("/api/users/{userId}/followers/count", invalidFolloweeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFolloweesCountShouldReturnFolloweesCountAnd200() throws Exception {
        long followerId = 1L;
        CountResponse followeesCount = new CountResponse(15L);

        when(subscriptionService.getFolloweesCount(followerId))
                .thenReturn(followeesCount);

        mockMvc.perform(get("/api/users/{userId}/followees/count", followerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.count").value(followeesCount.count()));
    }

    @Test
    void getFolloweesCountShouldReturn400_whenFollowerIdInvalid() throws Exception {
        long invalidFollowerId = -1L;

        mockMvc.perform(get("/api/users/{userId}/followers/count", invalidFollowerId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFollowersShouldReturnFilteredFollowersAnd200() throws Exception {
        long followeeId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "John", "123777000", 15, 30
        );

        List<UserDto> filteredFollowers = List.of(
                new UserDto(2L, "Johny", null, "11237770009", null),
                new UserDto(3L, "jOhN1", null, "212377700099", null)
        );

        when(subscriptionService.getFollowers(followeeId, userFiltersDto))
                .thenReturn(filteredFollowers);

        mockMvc.perform(get("/api/users/{userId}/followers"
                        + "?namePattern=John&phoneNumber=123777000&experienceMin=15&experienceMax=30", followeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(3L));
    }

    @Test
    void getFollowersShouldReturn400_whenFolloweeIdInvalid() throws Exception {
        long invalidFolloweeId = -1L;

        mockMvc.perform(get("/api/users/{userId}/followers", invalidFolloweeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFollowersShouldReturn400_whenUserExperienceFiltersInvalid() throws Exception {
        long followeeId = 1L;

        mockMvc.perform(get("/api/users/{userId}/followers"
                        + "?experienceMin=100&experienceMax=10", followeeId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFolloweesShouldReturnFilteredFolloweesAnd200() throws Exception {
        long followerId = 1L;
        UserFiltersDto userFiltersDto = new UserFiltersDto(
                "Cyntia", "336699", 30, 45
        );

        List<UserDto> filteredFollowees = List.of(
                new UserDto(2L, "CyntiaJJJ", null, "783366991", null),
                new UserDto(4L, "CyNtIa54", null, "2333669932", null)
        );

        when(subscriptionService.getFollowees(followerId, userFiltersDto))
                .thenReturn(filteredFollowees);

        mockMvc.perform(get("/api/users/{userId}/followees"
                        + "?namePattern=Cyntia&phoneNumber=336699&experienceMin=30&experienceMax=45", followerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[1].id").value(4L));
    }

    @Test
    void getFolloweesShouldReturn400_whenFollowerIdInvalid() throws Exception {
        long invalidFollowerId = -1L;

        mockMvc.perform(get("/api/users/{userId}/followees", invalidFollowerId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getFolloweesShouldReturn400_whenUserExperienceFiltersInvalid() throws Exception {
        long followerId = 1L;

        mockMvc.perform(get("/api/users/{userId}/followees?experienceMin=40&experienceMax=15", followerId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }
}
