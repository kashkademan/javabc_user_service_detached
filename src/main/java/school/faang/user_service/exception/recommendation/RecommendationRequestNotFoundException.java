package school.faang.user_service.exception.recommendation;

public class RecommendationRequestNotFoundException extends RuntimeException {
    public RecommendationRequestNotFoundException(String messageError) {
        super(messageError);
    }
}
