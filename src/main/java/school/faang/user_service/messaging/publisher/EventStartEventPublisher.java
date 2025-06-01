package school.faang.user_service.messaging.publisher;

import school.faang.user_service.messaging.event.EventStartEvent;

public interface EventStartEventPublisher {
    void publish(EventStartEvent event);
}
