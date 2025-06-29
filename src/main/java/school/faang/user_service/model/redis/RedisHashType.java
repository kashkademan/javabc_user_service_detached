package school.faang.user_service.model.redis;

import lombok.Getter;

@Getter
public enum RedisHashType {
    PROMOTION("PROMOTION"),
    EVENT("EVENT"),
    USER("USER");

    private final String hashName;

    RedisHashType(String hashName) {
        this.hashName = hashName;
    }
}
