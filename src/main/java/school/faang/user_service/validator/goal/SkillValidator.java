package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.skill.SkillNotExistException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillValidator {

    private final SkillRepository skillRepository;

    public void validateExistingSkills(List<Long> skillsId) {
        List<Long> absentSkillsId = skillsId.stream()
                .filter(skillId -> !skillRepository.existsById(skillId))
                .toList();

        if (!absentSkillsId.isEmpty()) {
            String notExistingSkillsId = absentSkillsId.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            throw new SkillNotExistException(notExistingSkillsId);
        }
    }
}