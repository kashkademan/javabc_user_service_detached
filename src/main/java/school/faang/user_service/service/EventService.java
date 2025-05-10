package school.faang.user_service.service;

import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    Event create(Event event, List<Long> eventSkillsIds, Long ownerId);
}
