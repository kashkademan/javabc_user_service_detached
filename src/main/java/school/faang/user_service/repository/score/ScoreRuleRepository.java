package school.faang.user_service.repository.score;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ScoreRuleRepository {

    private static final String GLOBAL_PREFIX = "score:rules:";
    private static final String ROLE_PREFIX = "score:rulesByRole:";

    @Autowired
    @Qualifier("integerRedisTemplate")
    private RedisTemplate<String, Integer> redisTemplate;

    public Integer getScore(String type) {
        return redisTemplate.opsForValue().get(GLOBAL_PREFIX + type);
    }

    public Integer getScoreByRole(String type, String role) {
        return redisTemplate.opsForValue().get(ROLE_PREFIX + type + ":" + role.toUpperCase());
    }

    public void setScore(String type, int value) {
        redisTemplate.opsForValue().set(GLOBAL_PREFIX + type, value);
    }

    public void setScoreByRole(String type, String role, int value) {
        redisTemplate.opsForValue().set(ROLE_PREFIX + type + ":" + role.toUpperCase(), value);
    }
}
