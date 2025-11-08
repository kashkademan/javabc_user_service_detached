package school.faang.user_service.dto.event;

public record EventDto(long actorId, long receiverId, String eventType) {
}

