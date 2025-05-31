package school.faang.user_service.util.redis;

import lombok.experimental.UtilityClass;
import school.faang.user_service.exception.redis.InvalidRedisKeyException;
import school.faang.user_service.model.redis.RedisHashType;

@UtilityClass
public class RedisKeyUtil {
    public static final String HASH_KEY_SEPARATOR = ": ";
    public static String generateKey(Long id, RedisHashType redisHashType) {
        return redisHashType.getHashName() + HASH_KEY_SEPARATOR + id;
    }

    public static String generateKey(String id, RedisHashType redisHashType) {
        return redisHashType.getHashName() + HASH_KEY_SEPARATOR + id;
    }

    public static Long extractId(String redisKey) {
        if (redisKey == null || !redisKey.contains(HASH_KEY_SEPARATOR)) {
            throw new InvalidRedisKeyException("Invalid Redis key: " + redisKey);
        }
        return Long.parseLong(redisKey.split(HASH_KEY_SEPARATOR)[1].trim());
    }
}
