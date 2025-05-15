package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public Skill getSkillByIdOrThrow(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> {
                    log.error("Skill with id {} not found", skillId);
                    return new SkillNotFoundException(skillId);
                });
    }

    @Transactional
    public void assignSkillsToUsers(List<Long> skillIds, List<Long> userIds) {
        skillIds.forEach(skillId ->
                userIds.forEach(userId ->
                        skillRepository.findUserSkill(skillId, userId)
                                .ifPresentOrElse(
                                        skill -> log.debug(
                                                "User with id {} already has skill with id {}", userId, skillId),
                                        () -> {
                                            skillRepository.assignSkillToUser(skillId, userId);
                                            log.info("Assigned skill with id {} to user with id {}", skillId, userId);
                                        }
                                )
                )
        );
    }
}
