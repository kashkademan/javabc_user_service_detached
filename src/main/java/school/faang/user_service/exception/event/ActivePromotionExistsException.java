package school.faang.user_service.exception.event;

public class ActivePromotionExistsException extends RuntimeException {
    public ActivePromotionExistsException(Long eventId) {
        super(String.format("Cannot delete event with id %d because it has an active promotion", eventId));
    }
}
