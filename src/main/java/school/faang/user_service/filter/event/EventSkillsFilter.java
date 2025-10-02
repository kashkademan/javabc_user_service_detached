package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.filter.Filter;

import java.util.HashSet;
import java.util.stream.Stream;

@Component
public class EventSkillsFilter implements Filter<Event, EventFilterDto> {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.skillIds() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events
                .filter(event -> new HashSet<>(event.getRelatedSkills().stream()
                        .map(Skill::getId)
                        .toList())
                        .containsAll(eventFilterDto.skillIds()));
    }
}
