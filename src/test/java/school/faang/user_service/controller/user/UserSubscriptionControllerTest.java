package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.service.user.UserSubscriptionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserSubscriptionControllerTest — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 24.07.2025
 */
@WebMvcTest(UserSubscriptionController.class)
public class UserSubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSubscriptionService service;

    @MockBean
    private UserContext userContext;

    @InjectMocks
    private UserSubscriptionController controller;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testFollowUser() throws Exception {
        long followeeId = 123L;

        mockMvc.perform(post("/subscriptions/follow/{followeeId}", followeeId))
                .andExpect(status().isNoContent());

        verify(service).followUser(followeeId);
    }

    @Test
    public void testUnfollowUser() throws Exception {
        long followeeId = 123L;

        mockMvc.perform(delete("/subscriptions/unfollow/{followeeId}", followeeId))
                .andExpect(status().isNoContent());

        verify(service).unfollowUser(followeeId);
    }

    @Test
    public void testGetFollowersCount() throws Exception {
        long followeeId = 123L;
        CountResponse response = new CountResponse(10);

        when(service.getFollowersCount(followeeId)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/followers/count")
                        .param("followeeId", String.valueOf(followeeId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(service).getFollowersCount(followeeId);
    }

    @Test
    public void testGetFolloweesCount() throws Exception {
        long followerId = 321L;
        CountResponse response = new CountResponse(7);

        when(service.getFolloweesCount(followerId)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/followees/count")
                        .param("followerId", String.valueOf(followerId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(service).getFolloweesCount(followerId);
    }

    @Test
    public void testGetFollowers() throws Exception {
        UserDto userDto = new UserDto(null, null, null, null, null);

        List<UserDto> filteredFollowers = List.of(userDto);

        when(service.getFollowers(eq(1L), any(UserFiltersDto.class))).thenReturn(filteredFollowers);

        mockMvc.perform(get("/subscriptions/followers")
                        .param("followeeId", "1")
                        .param("username", "name")
                        .param("phone", "123456789")
                        .param("experienceFrom", "2")
                        .param("experienceTo", "4"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filteredFollowers)));

        verify(service).getFollowers(eq(1L), any(UserFiltersDto.class));
    }

    @Test
    public void testGetFollowees() throws Exception {
        long followerId = 321L;
        UserDto userDto = new UserDto(null, null, null, null, null);

        List<UserDto> filteredFollowees = List.of(userDto);

        when(service.getFollowees(eq(followerId), any(UserFiltersDto.class))).thenReturn(filteredFollowees);

        mockMvc.perform(get("/subscriptions/followees")
                        .param("followerId", String.valueOf(followerId)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filteredFollowees)));

        verify(service).getFollowees(eq(followerId), any(UserFiltersDto.class));
    }
}