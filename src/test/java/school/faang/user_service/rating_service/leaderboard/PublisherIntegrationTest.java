package school.faang.user_service.rating_service.leaderboard;

import com.redis.testcontainers.RedisContainer;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.rating_service.dto.post.PostPublishedEvent;
import school.faang.user_service.rating_service.repository.UserScoreRepository;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;
import school.faang.user_service.rating_service.service.leaderboard.redis.RedisService;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Тестирование взаимодействия паблишера и базы данных (обновление баллов пользователя)
 *
 * @author Linempy
 * @since 12.09.2025
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PublisherIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private UserScoreRepository scoreRepository;

    @Autowired
    private PostgresService postgresService;

    @SpyBean
    private RedisService redisService;

    @Test
    void test() {

    }

    @Test
    @DisplayName("Проверка на сохранение баллов пользователя при ошибки соединения Redis")
    void testRedisFailure_FallbackToPostgres() throws Exception {
        Long userId = 2L;
        Double score = 20.0;
        PostPublishedEvent event = new PostPublishedEvent(1L, 2L, 3L);

        doThrow(new RedisConnectionException("Redis connection failed"))
                .when(redisService).incrementOrCreateUserScore(anyLong(), anyDouble());

        leaderboardService.processUpdateUserScore(event, score);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> {
                    Double userScore = postgresService.getUserScore(userId);
                    return userScore != null && userScore.equals(score);
                });

        Double userScore = postgresService.getUserScore(userId);
        assertEquals(score, userScore, 0.001);
        verify(redisService, atLeastOnce()).incrementOrCreateUserScore(userId, score);
    }

    @Test
    @DisplayName("Проверка на успешное обновление баллов пользователя")
    void testMultiplePostPublications_IncrementScore() {
        Long userId = 3L;
        Double firstPostScore = 15.0;
        Double secondPostScore = 15.0;
        Double expectedTotalScore = 30.0;

        PostPublishedEvent firstEvent = new PostPublishedEvent(101L, userId, 4L);
        PostPublishedEvent secondEvent = new PostPublishedEvent(102L, userId, 4L);

        leaderboardService.processUpdateUserScore(firstEvent, firstPostScore);
        leaderboardService.processUpdateUserScore(secondEvent, secondPostScore);

        await().atMost(5, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> {
                    Double totalScore = postgresService.getUserScore(userId);
                    return totalScore != null && totalScore.equals(expectedTotalScore);
                });

        Double finalScore = postgresService.getUserScore(userId);
        assertEquals(expectedTotalScore, finalScore, 0.001);
    }
}