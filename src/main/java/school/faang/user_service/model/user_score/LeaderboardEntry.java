package school.faang.user_service.model.user_score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Leaderboard")
@Getter
@Setter
public class LeaderboardEntry {
    @Id
    private long userId;
    private int totalScore;
}
