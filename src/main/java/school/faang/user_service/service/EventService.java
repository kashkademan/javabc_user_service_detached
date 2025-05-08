package school.faang.user_service.service;

import school.faang.user_service.dto.event.EventDto;

public interface EventService {
    EventDto create(EventDto event);
}
