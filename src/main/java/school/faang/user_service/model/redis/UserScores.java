package school.faang.user_service.model.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("UserScores")
@Getter
@Setter
public class UserScores {
    @Id
    private long userId;
    private int scoreDelta;
}
