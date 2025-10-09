package school.faang.user_service.validator.event;

import org.springframework.util.ObjectUtils;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class EventValidator {
    public static void validateOwner(Event event, long currentUserId) {
        if (event.getOwner() == null) {
            throw new IllegalArgumentException("");
        }
        if (!Objects.equals(currentUserId, event.getOwner().getId())) {
            throw new ForbiddenException("Only owner can modify event");
        }
    }

    public static void validateOwnerSkills(User owner, Set<Long> skillsId) {
        if (ObjectUtils.isEmpty(skillsId)) {
            return;
        }

        Set<Long> ownerSkillsId = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> missingSkills = skillsId.stream()
                .filter(id -> !ownerSkillsId.contains(id))
                .collect(Collectors.toSet());

        if (!missingSkills.isEmpty()) {
            throw new DataValidationException("Owner does not have required skills: " + missingSkills);
        }
    }

    public static void validateEventCreation(CreateEventDto dto, User owner, LocalDateTime now) {
        validateOwnerSkills(owner, dto.skillsId());
        validateEventDates(dto.startDate(), dto.endDate());
    }

    public static void validateEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new DataValidationException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new DataValidationException("Start date must be before end date");
        }
    }
}