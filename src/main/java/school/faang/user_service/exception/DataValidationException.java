package school.faang.user_service.exception;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String massage) {
        super(String.valueOf(message));
    }
}