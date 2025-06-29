package school.faang.user_service.model.score;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("leaderboard")
@AllArgsConstructor
public class LeaderboardEntry {
    @Id
    private long userId;
    private double totalScore;
}
