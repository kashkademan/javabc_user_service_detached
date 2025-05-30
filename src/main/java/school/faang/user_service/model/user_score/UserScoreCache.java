package school.faang.user_service.model.user_score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserScores")
@Getter
@Setter
public class UserScoreCache {
    @Id
    private long userId;
    private int scoreDelta;
}
