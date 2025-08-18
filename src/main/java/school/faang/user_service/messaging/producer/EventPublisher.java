package school.faang.user_service.messaging.producer;

/**
 * EventProducer — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface EventPublisher<E> {
    void publish(E event);
}
