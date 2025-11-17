package school.faang.user_service.publisher;

public interface MessagePublisher<T> {

    void publish(T event);

    Class<?> getInstanceClass();
}
