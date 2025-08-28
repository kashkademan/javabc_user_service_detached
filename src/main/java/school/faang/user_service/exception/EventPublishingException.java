package school.faang.user_service.exception;

/**
 * EventPublishingException — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 28.08.2025
 */
public class EventPublishingException extends Throwable {
    public EventPublishingException(String message, Exception e) {
        super(message, e);
    }
}