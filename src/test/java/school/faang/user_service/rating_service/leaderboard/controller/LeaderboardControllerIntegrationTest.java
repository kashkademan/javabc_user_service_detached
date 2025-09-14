package school.faang.user_service.rating_service.leaderboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.CUSTOM_SIZE;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.DEFAULT_PAGE;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.EXISTING_USER_ID;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.EXPECTED_PAGINATED_USERS;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.EXPECTED_TOP_USERS;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.LEADERBOARD_KEY;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.NON_EXISTENT_PAGE;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.NON_EXISTENT_USER_ID;
import static school.faang.user_service.rating_service.leaderboard.controller.LeaderboardControllerTestData.USER_SCORES;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class LeaderboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().serverCommands().flushAll();

        jdbcTemplate.execute("DELETE FROM user_score");

        for (Map<String, Object> userScore : USER_SCORES) {
            jdbcTemplate.update(
                    "INSERT INTO user_score (user_id, score) VALUES (?, ?)",
                    userScore.get("user_id"),
                    userScore.get("score")
            );
        }

        ZSetOperations<String, Object> usersScore = redisTemplate.opsForZSet();
        for (Map<String, Object> userScore : USER_SCORES) {
            usersScore.add(
                    LEADERBOARD_KEY,
                    userScore.get("user_id"),
                    (Double) userScore.get("score")
            );
        }
    }

    @Test
    @DisplayName("GET /leaderboard/top должен возвращать топ пользователей по умолчанию")
    void getTopUsersScore_WithDefaultPagination() throws Exception {
        mockMvc.perform(get("/leaderboard/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(USER_SCORES.size())))
                .andExpect(jsonPath("$[0].userId", is(EXPECTED_TOP_USERS.get(0).get("userId"))))
                .andExpect(jsonPath("$[0].score", is(EXPECTED_TOP_USERS.get(0).get("score"))))
                .andExpect(jsonPath("$[1].userId", is(EXPECTED_TOP_USERS.get(1).get("userId"))))
                .andExpect(jsonPath("$[1].score", is(EXPECTED_TOP_USERS.get(1).get("score"))))
                .andExpect(jsonPath("$[2].userId", is(EXPECTED_TOP_USERS.get(2).get("userId"))))
                .andExpect(jsonPath("$[2].score", is(EXPECTED_TOP_USERS.get(2).get("score"))))
                .andExpect(jsonPath("$[3].userId", is(EXPECTED_TOP_USERS.get(3).get("userId"))))
                .andExpect(jsonPath("$[3].score", is(EXPECTED_TOP_USERS.get(3).get("score"))))
                .andExpect(jsonPath("$[4].userId", is(EXPECTED_TOP_USERS.get(4).get("userId"))))
                .andExpect(jsonPath("$[4].score", is(EXPECTED_TOP_USERS.get(4).get("score"))));
    }

    @Test
    @DisplayName("GET /leaderboard/top с параметрами пагинации должен возвращать корректную страницу")
    void getTopUsersScore_withParams_shouldReturnCorrectPage() throws Exception {
        mockMvc.perform(get("/leaderboard/top")
                        .param("page", String.valueOf(DEFAULT_PAGE))
                        .param("size", String.valueOf(CUSTOM_SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(CUSTOM_SIZE)))
                .andExpect(jsonPath("$[0].userId", is(EXPECTED_PAGINATED_USERS.get(0).get("userId"))))
                .andExpect(jsonPath("$[0].score", is(EXPECTED_PAGINATED_USERS.get(0).get("score"))))
                .andExpect(jsonPath("$[1].userId", is(EXPECTED_PAGINATED_USERS.get(1).get("userId"))))
                .andExpect(jsonPath("$[1].score", is(EXPECTED_PAGINATED_USERS.get(1).get("score"))));
    }

    @Test
    @DisplayName("GET /leaderboard/top с несуществующей страницей должен возвращать пустой список")
    void getTopUsersScore_withNotExistPage_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/leaderboard/top")
                        .param("page", String.valueOf(NON_EXISTENT_PAGE))
                        .param("size", String.valueOf(CUSTOM_SIZE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /leaderboard/{userId} должен возвращать счет пользователя")
    void getUserScore_shouldReturnUserScore() throws Exception {
        mockMvc.perform(get("/leaderboard/{userId}", EXISTING_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(EXISTING_USER_ID.intValue())))
                .andExpect(jsonPath("$.score", is(200.0)));
    }

    @Test
    @DisplayName("GET /leaderboard/{userId} с несуществующим пользователем должен возвращать 0 score")
    void getUserScore_withNotExistUser_shouldReturnZeroScore() throws Exception {
        mockMvc.perform(get("/leaderboard/{userId}", NON_EXISTENT_USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(NON_EXISTENT_USER_ID.intValue())))
                .andExpect(jsonPath("$.score", is(0.0)));
    }

    @Test
    @DisplayName("GET /leaderboard/top с size=1 должен возвращать только одного пользователя")
    void getTopUsersScore_withSizeOne_shouldReturnSingleUser() throws Exception {
        mockMvc.perform(get("/leaderboard/top")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(EXPECTED_TOP_USERS.get(0).get("userId"))))
                .andExpect(jsonPath("$[0].score", is(EXPECTED_TOP_USERS.get(0).get("score"))));
    }
}