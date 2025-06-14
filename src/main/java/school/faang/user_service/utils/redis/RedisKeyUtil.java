package school.faang.user_service.utils.redis;

import lombok.experimental.UtilityClass;
import school.faang.user_service.exception.redis.InvalidRedisKeyException;
import school.faang.user_service.model.redis.RedisHashType;

@UtilityClass
public class RedisKeyUtil {
    private static final String HASH_KEY_SEPARATOR = ":";
    private static final String LOCK = "lock";
    public static String getKeyById(Long id, RedisHashType redisHashType) {
        return redisHashType.getHashName() + HASH_KEY_SEPARATOR + id;
    }

    public static String getLockNameByKey(String key) {
        return LOCK + HASH_KEY_SEPARATOR + key;
    }

    public static Long getIdByKey(String redisKey) {
        if (redisKey == null || !redisKey.contains(HASH_KEY_SEPARATOR)) {
            throw new InvalidRedisKeyException("Invalid Redis key: " + redisKey);
        }
        return Long.parseLong(redisKey.split(HASH_KEY_SEPARATOR)[1].trim());
    }
}
