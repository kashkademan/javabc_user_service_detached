package school.faang.user_service.messaging.consumer;

/**
 * EventListener — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface EventConsumer<E> {
    void listen(E event);
}
