package school.faang.user_service.controller.leaderboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.rating_service.config.LeaderDto;
import school.faang.user_service.rating_service.controller.LeaderBoardController;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LeaderBoardController.class)
class LeaderBoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ZSetOperations<String, String> zsetOperations;

    @Test
    void getTopLeaders_validData_returnsLeaders() throws Exception {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);

        var tuple1 = LeaderBoardControllerTestData.tuple("1", 100.0);
        var tuple2 = LeaderBoardControllerTestData.tuple("2", 200.0);

        when(zsetOperations.reverseRangeWithScores("leaderboard", 0, 9))
                .thenReturn(Set.of(tuple1, tuple2));

        UserIdUsernameProjection u1 = LeaderBoardControllerTestData.user(1L, "Alice");
        UserIdUsernameProjection u2 = LeaderBoardControllerTestData.user(2L, "Bob");

        when(userRepository.findByIdIn(List.of(1L, 2L)))
                .thenReturn(List.of(u1, u2));

        var mvcResult = mockMvc.perform(get("/leaders/top?limit=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = mvcResult.getResponse().getContentAsString();

        List<LeaderDto> actual = List.of(
                new LeaderDto(2L, "Bob", 200L),
                new LeaderDto(1L, "Alice", 100L)
        );

        assertThat(responseJson).contains("Bob", "Alice", "200", "100");
    }
}
