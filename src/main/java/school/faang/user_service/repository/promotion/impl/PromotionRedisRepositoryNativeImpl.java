package school.faang.user_service.repository.promotion.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.repository.promotion.PromotionRedisRepositoryNative;

@RequiredArgsConstructor
public class PromotionRedisRepositoryNativeImpl implements PromotionRedisRepositoryNative {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public long decrementCountView(String key) {
        String redisKey = "promotion:" + key;
        return redisTemplate.opsForHash().increment(redisKey, "countView", -1L);
    }
}
