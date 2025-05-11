package school.faang.user_service.validation.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EventCreationNotAllowedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EventValidation {
    public void validateUserHasAllEventSkills(List<Long> eventSkillsIds, User owner) {
        List<Skill> ownersSkills = owner.getSkills();
        Set<Long> ownersSkillsIds = new HashSet<>(
                ownersSkills.stream()
                        .map(Skill::getId)
                        .toList()
        );
        if (eventSkillsIds != null && !eventSkillsIds.isEmpty()) {
            Set<Long> requiredSkillsIds = new HashSet<>(eventSkillsIds);
            requiredSkillsIds.removeAll(ownersSkillsIds);

            if (!requiredSkillsIds.isEmpty()) {
                throw new EventCreationNotAllowedException(
                        String.format("Недостаточно навыков для создания данного мероприятия. Отсутствуют навыки: %s",
                                requiredSkillsIds)
                );
            }
        }
    }
}
