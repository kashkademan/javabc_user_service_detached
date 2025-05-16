package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.SkillNotExistException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public Skill getSkillById(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillNotExistException(skillId));
    }

    @Transactional
    public void removeSkillForGoal(long goalId) {
        skillRepository.removeSkillsFromGoal(goalId);
    }

    @Transactional
    public void assignSkillsToUser(long userId, List<Skill> skills) {
        List<Skill> ownedSkills = skillRepository.findAllByUserId(userId);
        skills
                .stream()
                .filter(skill -> !ownedSkills.contains(skill))
                .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), userId));
    }

    @Transactional(readOnly = true)
    public List<Skill> getSkillsById(List<Long> skillsId) {
        return skillsId.stream()
                .map(this::getSkillById)
                .collect(Collectors.toList());
    }
}