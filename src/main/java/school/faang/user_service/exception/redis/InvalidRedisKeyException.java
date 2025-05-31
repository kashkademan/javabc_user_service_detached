package school.faang.user_service.exception.redis;

public class InvalidRedisKeyException extends RuntimeException {
    public InvalidRedisKeyException(String message) {
        super(message);
    }
}
