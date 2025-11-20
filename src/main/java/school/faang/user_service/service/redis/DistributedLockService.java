package school.faang.user_service.service.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DistributedLockService {

    private final RedisTemplate<String, String> redisTemplate;

    public DistributedLockService(@Qualifier("customStringRedisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryLock(String lockKey, Duration timeout) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, "locked", timeout)
        );
    }

    public void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
