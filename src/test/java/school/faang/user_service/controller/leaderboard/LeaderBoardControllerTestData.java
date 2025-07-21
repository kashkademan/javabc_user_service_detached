package school.faang.user_service.controller.leaderboard;

import org.springframework.data.redis.core.ZSetOperations;
import school.faang.user_service.rating_service.rating_aspect.UserIdUsernameProjection;

public class LeaderBoardControllerTestData {

    public static UserIdUsernameProjection user(long id, String name) {
        return new UserIdUsernameProjection() {
            @Override
            public Long getId() {
                return id;
            }

            @Override
            public String getUsername() {
                return name;
            }
        };
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
                return Double.compare(o.getScore(), this.getScore());
            }
        };
    }
}