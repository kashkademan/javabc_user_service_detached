package school.faang.user_service.model.score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("user_scores")
public class UserScoreCache {
    @Id
    private long userId;
    private int scoreDelta;
}
