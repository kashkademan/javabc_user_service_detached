package school.faang.user_service.dto.recommendation;

public record UpdateRecommendationDto(
        Long recommendationId,
        String content
) {}
