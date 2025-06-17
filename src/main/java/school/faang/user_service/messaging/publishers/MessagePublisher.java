package school.faang.user_service.messaging.publishers;

public interface MessagePublisher <T> {
     void publishMessage(T t);
}
