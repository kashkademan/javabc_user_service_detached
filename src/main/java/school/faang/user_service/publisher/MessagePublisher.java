package school.faang.user_service.publisher;

public interface MessagePublisher {
  void publish(Object message);
}