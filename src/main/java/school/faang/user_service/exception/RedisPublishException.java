package school.faang.user_service.exception;

public class RedisPublishException extends RuntimeException {
    public RedisPublishException(String message) {
        super(message);
    }
}
