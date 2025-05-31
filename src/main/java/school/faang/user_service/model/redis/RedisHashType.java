package school.faang.user_service.model.redis;

import lombok.Getter;

@Getter
public enum RedisHashType {
    PROMOTION("PROMOTION"),
    EVENT("EVENT");

    private final String hashName;

    RedisHashType(String hashName) {
        this.hashName = hashName;
    }
}
