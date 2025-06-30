package school.faang.user_service.dto.event;

public record RecommendationEvent(
        long authorId,
        long receiverId,
        String text
) {}
