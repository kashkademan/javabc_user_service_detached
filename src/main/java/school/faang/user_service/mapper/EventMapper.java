package school.faang.user_service.mapper;

import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public interface EventMapper {

    static Event toEvent(EventCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setTitle(dto.title());
        event.setDescription(dto.description());
        event.setStartDate(dto.startDate());
        event.setEndDate(dto.endDate());
        event.setType(dto.type());
        event.setRelatedSkills(mapSkills(dto.skillsId()));

        return event;
    }

    static void updateEvent(EventUpdateDto dto, Event event) {
        if (dto == null || event == null) {
            return;
        }

        Optional.ofNullable(dto.title()).ifPresent(event::setTitle);
        Optional.ofNullable(dto.description()).ifPresent(event::setDescription);
        Optional.ofNullable(dto.startDate()).ifPresent(event::setStartDate);
        Optional.ofNullable(dto.endDate()).ifPresent(event::setEndDate);
        Optional.ofNullable(dto.type()).ifPresent(event::setType);

        if (dto.skillsId() != null) {
            event.setRelatedSkills(mapSkills(dto.skillsId()));
        }
    }

    static EventDto toEventDto(Event event) {
        if (event == null) {
            return null;
        }

        User owner = event.getOwner();

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .status(event.getStatus())
                .type(event.getType())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .ownerId(owner != null ? owner.getId() : null)
                .skills(mapSkillTitles(event.getRelatedSkills()))
                .build();
    }

    private static List<Skill> mapSkills(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
                .collect(Collectors.toList());
    }

    private static Set<String> mapSkillTitles(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Set.of();
        }
        return skills.stream()
                .map(Skill::getTitle)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}