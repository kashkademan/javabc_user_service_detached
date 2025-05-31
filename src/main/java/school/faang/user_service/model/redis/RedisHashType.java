package school.faang.user_service.model.redis;

import lombok.Getter;

@Getter
public enum RedisHashType {
    PROMOTION("promotion"),
    EVENT_PROMOTION("event_promotion"),
    USER_PROMOTION("user_promotion");

    private final String hashName;

    RedisHashType(String hashName) {
        this.hashName = hashName;
    }
}
