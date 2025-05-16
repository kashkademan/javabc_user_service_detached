package school.faang.user_service.exception.recommendation;

public class RecommendationRequestValidationException extends RuntimeException {
    public RecommendationRequestValidationException(String messageError) {
        super(messageError);
    }
}
