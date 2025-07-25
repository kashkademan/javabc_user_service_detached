package school.faang.user_service.controller.leaderboard;

import org.springframework.data.redis.core.ZSetOperations;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;

import java.util.List;
import java.util.Set;

public class LeaderBoardControllerTestData {
    public static Set<ZSetOperations.TypedTuple<String>> tuples() {
        return Set.of(
                tuple("2", 200.0),
                tuple("1", 100.0)
        );
    }

    public static List<UserIdUsernameProjection> users() {
        return List.of(
                user(1L, "Alice"),
                user(2L, "Bob")
        );
    }

    public static ZSetOperations.TypedTuple<String> tuple(String value, double score) {
        return new ZSetOperations.TypedTuple<>() {
            @Override
            public String getValue() {
                return value;
            }

            @Override
            public Double getScore() {
                return score;
            }

            @Override
            public int compareTo(ZSetOperations.TypedTuple<String> o) {
                return Double.compare(score, o.getScore());
            }
        };
    }

    public static UserIdUsernameProjection user(Long id, String username) {
        return new UserIdUsernameProjection() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getUsername() {
                return username;
            }
        };
    }
}