package school.faang.user_service.exception;

public class RecommendationRequestNotFoundException extends RuntimeException {
    public RecommendationRequestNotFoundException(MessageError messageError) {
        super(messageError.getMessage());
    }
}
