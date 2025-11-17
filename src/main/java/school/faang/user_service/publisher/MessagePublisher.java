package school.faang.user_service.publisher;

import school.faang.user_service.dto.event.SearchAppearanceEvent;

public interface MessagePublisher<T> {

    void publish(T event);

    Class<?> getInstanceClass();
}
