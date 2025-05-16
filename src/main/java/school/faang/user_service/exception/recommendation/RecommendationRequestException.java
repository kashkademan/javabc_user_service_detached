package school.faang.user_service.exception.recommendation;

public class RecommendationRequestException extends RuntimeException {
    public RecommendationRequestException(String messageError) {
        super(messageError);
    }
}
