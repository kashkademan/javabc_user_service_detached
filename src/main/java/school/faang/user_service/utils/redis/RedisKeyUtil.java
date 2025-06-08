package school.faang.user_service.utils.redis;

import lombok.experimental.UtilityClass;
import school.faang.user_service.exception.redis.InvalidRedisKeyException;
import school.faang.user_service.model.redis.RedisHashType;

import java.util.UUID;

@UtilityClass
public class RedisKeyUtil {
    public static final String HASH_KEY_SEPARATOR = ": ";
    public static UUID getKeyById(Long id, RedisHashType redisHashType) {
        return UUID.fromString(redisHashType.getHashName() + HASH_KEY_SEPARATOR + id);
    }

    public static Long getIdByKey(UUID redisKey) {
        String stringKey = redisKey.toString();
        if (stringKey == null || !stringKey.contains(HASH_KEY_SEPARATOR)) {
            throw new InvalidRedisKeyException("Invalid Redis key: " + redisKey);
        }
        return Long.parseLong(stringKey.split(HASH_KEY_SEPARATOR)[1].trim());
    }
}
