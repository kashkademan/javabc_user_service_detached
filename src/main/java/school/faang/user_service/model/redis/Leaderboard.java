package school.faang.user_service.model.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Leaderboard")
@Getter
@Setter
public class Leaderboard {
    @Id
    private long userId;
    private int totalScore;
}
