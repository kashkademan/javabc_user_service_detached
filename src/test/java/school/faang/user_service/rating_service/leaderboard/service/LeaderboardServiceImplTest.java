package school.faang.user_service.rating_service.leaderboard.service;

import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ZSetOperations;
import school.faang.user_service.rating_service.dto.user.UserScoreViewDto;
import school.faang.user_service.rating_service.entity.ScorableEvent;
import school.faang.user_service.rating_service.mapper.UserScoreMapper;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardServiceImpl;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;
import school.faang.user_service.rating_service.service.leaderboard.redis.RedisService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование LeaderboardService")
@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock
    private PostgresService postgresService;

    @Mock
    private RedisService redisService;

    @Mock
    private UserScoreMapper mapper;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    @Test
    @DisplayName("Проверка на корректность обновления баллов пользователя")
    void testProcessUpdateUserScoreSuccess() {
        ScorableEvent event = mock(ScorableEvent.class);
        when(event.getUserId()).thenReturn(1L);
        Double earnedScore = 10.0;

        leaderboardService.processUpdateUserScore(event, earnedScore);

        verify(redisService).incrementOrCreateUserScore(1L, earnedScore);
        verify(postgresService).upsertUserScore(event, earnedScore);
    }

    @Test
    @DisplayName("Проверка на корректность обновления баллов при ошибки соединении Redis.")
    void testProcessUpdateScoreWhenFallbackToPostgres() {
        ScorableEvent event = mock(ScorableEvent.class);
        when(event.getUserId()).thenReturn(1L);
        Double earnedScore = 10.0;

        doThrow(new RedisConnectionException("Redis connection failed"))
                .when(redisService).incrementOrCreateUserScore(anyLong(), anyDouble());

        leaderboardService.processUpdateUserScore(event, earnedScore);

        verify(redisService).incrementOrCreateUserScore(1L, earnedScore);
        verify(postgresService).upsertUserScore(event, earnedScore);
    }

    @Test
    @DisplayName("Получения лидеров по валидным параметрам")
    void testGetTopScoresValidParameters() {
        int size = 10;
        int page = 0;

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(1L);
        when(tuple.getScore()).thenReturn(100.0);

        when(redisService.getTopUsers(page, size)).thenReturn(Set.of(tuple));
        when(mapper.getDtoByFields(1L, 100.0)).thenReturn(
                new UserScoreViewDto(1L, 100.0)
        );

        List<UserScoreViewDto> result = leaderboardService.getTopScores(size, page);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).userId());
        assertEquals(100.0, result.get(0).score());
    }

    @Test
    @DisplayName("Получения лидеров по невалидным параметрам")
    void testGetTopScoresInvalidParameters() {
        List<UserScoreViewDto> result = leaderboardService.getTopScores(-1, -1);

        assertTrue(result.isEmpty());
        verifyNoInteractions(redisService, mapper);
    }

    @Test
    @DisplayName("Проверка на получения пользователя, если он есть в Redis")
    void testGetUserScoreUserExistsInRedis() {
        Long userId = 1L;
        Double score = 50.0;

        when(redisService.getScoreByUserId(userId)).thenReturn(score);
        when(mapper.getDtoByFields(userId, score)).thenReturn(
                new UserScoreViewDto(userId, score)
        );

        UserScoreViewDto result = leaderboardService.getUserScore(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(score, result.score());
    }

    @Test
    @DisplayName("Проверка получения пользователя если его нет в Redis, но есть в Postgres")
    void testGetUserScoreUserNotInRedisFallbackToPostgres() {
        Long userId = 1L;
        Double postgresScore = 25.0;

        when(redisService.getScoreByUserId(userId)).thenReturn(postgresScore);

        when(mapper.getDtoByFields(userId, postgresScore)).thenReturn(
                new UserScoreViewDto(userId, postgresScore)
        );

        UserScoreViewDto result = leaderboardService.getUserScore(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(postgresScore, result.score());

        verify(postgresService, never()).getUserScore(userId);
    }

    @Test
    @DisplayName("Проверка получения баллов у несуществующего пользователя в Postgres")
    void testGetUserScoreUserNotFoundReturnsZeroScore() {
        Long userId = 1L;

        when(redisService.getScoreByUserId(userId)).thenReturn(0.0d);
        when(mapper.getDtoByFields(userId, 0.0)).thenReturn(
                new UserScoreViewDto(userId, 0.0)
        );

        UserScoreViewDto result = leaderboardService.getUserScore(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(0.0, result.score());

        verify(postgresService, never()).getUserScore(userId);
    }
}