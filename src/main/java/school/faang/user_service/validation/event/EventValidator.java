package school.faang.user_service.validation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final SkillService skillService;

    public void validateEventDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new EventValidationException("Дата окончания события не может быть раньше даты начала");
        }
    }

    public void validateOwnerHasSkills(Long userId, List<Skill> skills) {
        Set<Long> userSkillIds = skillService.getSkillsByUserId(userId).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> required = skills.stream().map(Skill::getId).collect(Collectors.toSet());

        if (!userSkillIds.containsAll(required)) {
            throw new EventValidationException("Пользователь не обладает всеми необходимыми навыками");
        }
    }
}
