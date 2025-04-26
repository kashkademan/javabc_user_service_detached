package school.faang.user_service.messaging;

public interface EventPublisher<T> {
    void publish(T event);
}
