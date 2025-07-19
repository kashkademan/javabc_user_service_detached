package school.faang.user_service.controller.leaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import school.faang.user_service.rating_service.controller.LeaderboardController;
import school.faang.user_service.rating_service.config.LeaderDto;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LeaderboardControllerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zsetOperations;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderboardController leaderboardController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);
    }

    @Test
    void getTopLeaders_emptyRedis_returnsEmptyList() {
        when(zsetOperations.reverseRangeWithScores(anyString(), anyLong(), anyLong()))
                .thenReturn(null);

        List<LeaderDto> leaders = leaderboardController.getTopLeaders(10);

        assertNotNull(leaders);
        assertTrue(leaders.isEmpty());

        verify(zsetOperations).reverseRangeWithScores("leaderboard", 0, 9);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getTopLeaders_validData_returnsLeadersWithUsernamesSorted() {
        var tuple1 = mock(ZSetOperations.TypedTuple.class);
        var tuple2 = mock(ZSetOperations.TypedTuple.class);

        when(tuple1.getValue()).thenReturn("1");
        when(tuple1.getScore()).thenReturn(100.0);
        when(tuple2.getValue()).thenReturn("2");
        when(tuple2.getScore()).thenReturn(200.0);

        Set<ZSetOperations.TypedTuple<String>> tuples = Set.of(tuple1, tuple2);

        when(zsetOperations.reverseRangeWithScores("leaderboard", 0, 9))
                .thenReturn(tuples);

        UserIdUsernameProjection u1 = new UserIdUsernameProjection() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getUsername() {
                return "Alice";
            }
        };
        UserIdUsernameProjection u2 = new UserIdUsernameProjection() {
            @Override
            public Long getId() {
                return 2L;
            }

            @Override
            public String getUsername() {
                return "Bob";
            }
        };

        when(userRepository.findUsernamesByIds(eq(List.of(1L, 2L))))
                .thenReturn(List.of(u1, u2));

        List<LeaderDto> leaders = leaderboardController.getTopLeaders(10);

        assertEquals(2, leaders.size());

        assertEquals(2L, leaders.get(0).getUserId());
        assertEquals("Bob", leaders.get(0).getUserName());
        assertEquals(200L, leaders.get(0).getScore());

        assertEquals(1L, leaders.get(1).getUserId());
        assertEquals("Alice", leaders.get(1).getUserName());
        assertEquals(100L, leaders.get(1).getScore());

        verify(zsetOperations).reverseRangeWithScores("leaderboard", 0, 9);
        verify(userRepository).findUsernamesByIds(eq(List.of(1L, 2L)));
    }
}
