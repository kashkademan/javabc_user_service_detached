package school.faang.user_service.exception;

public class JsonDeserializationException extends RuntimeException {

    public JsonDeserializationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
