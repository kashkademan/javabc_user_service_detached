package school.faang.user_service.rating_service.leaderboard.controller;

import java.util.List;
import java.util.Map;

/**
 * Класс с тестовыми данными для {@link LeaderboardControllerIntegrationTest}
 *
 * @author Linempy
 * @since 12.09.2025
 */
public class LeaderboardControllerTestData {

    public static final String LEADERBOARD_KEY = "global_leaderboard";

    public static final List<Map<String, Object>> USER_SCORES = List.of(
            Map.of("user_id", 1L, "score", 100.0),
            Map.of("user_id", 2L, "score", 200.0),
            Map.of("user_id", 3L, "score", 150.0),
            Map.of("user_id", 4L, "score", 300.0),
            Map.of("user_id", 5L, "score", 250.0)
    );

    public static final List<Object[]> USER_SCORES_ARRAY = List.of(
            new Object[]{1L, 100.0},
            new Object[]{2L, 200.0},
            new Object[]{3L, 150.0},
            new Object[]{4L, 300.0},
            new Object[]{5L, 250.0}
    );

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 50;
    public static final int CUSTOM_SIZE = 2;
    public static final int NON_EXISTENT_PAGE = 10;

    public static final Long EXISTING_USER_ID = 2L;
    public static final Long NON_EXISTENT_USER_ID = 999L;

    public static final Map<Integer, Map<String, Object>> EXPECTED_TOP_USERS = Map.of(
            0, Map.of("userId", 4, "score", 300.0),
            1, Map.of("userId", 5, "score", 250.0),
            2, Map.of("userId", 2, "score", 200.0),
            3, Map.of("userId", 3, "score", 150.0),
            4, Map.of("userId", 1, "score", 100.0)
    );

    public static final Map<Integer, Map<String, Object>> EXPECTED_PAGINATED_USERS = Map.of(
            0, Map.of("userId", 4, "score", 300.0),
            1, Map.of("userId", 5, "score", 250.0)
    );
}