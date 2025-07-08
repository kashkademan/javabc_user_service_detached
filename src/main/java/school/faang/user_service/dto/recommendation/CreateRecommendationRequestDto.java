package school.faang.user_service.dto.recommendation;

public record CreateRecommendationRequestDto(
        String message,
        Long receiverId
) {
}
