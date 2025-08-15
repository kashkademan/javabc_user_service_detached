package school.faang.user_service.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserViewDto;
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
    private static final long FOLLOWEE_ID = 123L;
    private static final long FOLLOWER_ID = 321L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSubscriptionService service;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("успешная подписка на пользователя по id")
    @Test
    public void testFollowUserSuccess() throws Exception {
        mockMvc.perform(post("/subscriptions/follow/{followeeId}", FOLLOWEE_ID))
                .andExpect(status().isNoContent());

        verify(service).followUser(FOLLOWEE_ID);
    }

    @DisplayName("успешная отписка от пользователя по id")
    @Test
    public void testUnfollowUserSuccess() throws Exception {
        mockMvc.perform(delete("/subscriptions/unfollow/{followeeId}", FOLLOWEE_ID))
                .andExpect(status().isNoContent());

        verify(service).unfollowUser(FOLLOWEE_ID);
    }

    @DisplayName("получение количества подписчиков у пользователя")
    @Test
    public void testGetFollowersCount() throws Exception {
        CountResponse response = new CountResponse(10);

        when(service.getFollowersCount(FOLLOWEE_ID)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/followers/count")
                        .param("followeeId", String.valueOf(FOLLOWEE_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(service).getFollowersCount(FOLLOWEE_ID);
    }

    @DisplayName("получение количества подписок у пользователя")
    @Test
    public void testGetFolloweesCount() throws Exception {
        CountResponse response = new CountResponse(7);

        when(service.getFolloweesCount(FOLLOWER_ID)).thenReturn(response);

        mockMvc.perform(get("/subscriptions/followees/count")
                        .param("followerId", String.valueOf(FOLLOWER_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(service).getFolloweesCount(FOLLOWER_ID);
    }

    @DisplayName("получение подписчиков пользователя")
    @Test
    public void testGetFollowers() throws Exception {
        UserViewDto userViewDto = new UserViewDto(null, null, null, null, null, null);

        List<UserViewDto> filteredFollowers = List.of(userViewDto);

        when(service.getFollowers(eq(1L), any(UserFiltersDto.class))).thenReturn(filteredFollowers);

        mockMvc.perform(get("/subscriptions/followers")
                        .param("followeeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filteredFollowers)));

        verify(service).getFollowers(eq(1L), any(UserFiltersDto.class));
    }

    @DisplayName("получение юзеров на которых подписан пользователь")
    @Test
    public void testGetFollowees() throws Exception {
        UserViewDto userViewDto = new UserViewDto(null, null, null, null, null, null);

        List<UserViewDto> filteredFollowees = List.of(userViewDto);

        when(service.getFollowees(eq(FOLLOWER_ID), any(UserFiltersDto.class))).thenReturn(filteredFollowees);

        mockMvc.perform(get("/subscriptions/followees")
                        .param("followerId", String.valueOf(FOLLOWER_ID)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(filteredFollowees)));

        verify(service).getFollowees(eq(FOLLOWER_ID), any(UserFiltersDto.class));
    }
}