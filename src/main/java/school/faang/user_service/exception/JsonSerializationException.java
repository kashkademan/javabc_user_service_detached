package school.faang.user_service.exception;

public class JsonSerializationException extends RuntimeException {

    public JsonSerializationException(String message, Object... args) {
      super(String.format(message, args));
    }
}
