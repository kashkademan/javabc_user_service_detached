package school.faang.user_service.exception;

public class KafkaSendMessageException extends RuntimeException {
  public KafkaSendMessageException(String message) {
    super(message);
  }
}
