package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;

    @Transactional(readOnly = true)
    public Skill create(Skill skill) {
        skillValidator.validateTitleBlank(skill.getTitle());
        skillValidator.validateTitleUnique(skill.getTitle());
        Skill createdSkill = skillRepository.save(skill);
        log.info("Созданный навык {}", createdSkill);
        return createdSkill;
    }

    @Transactional(readOnly = true)
    public List<Skill> getUserSkills(long userId) {
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);
        log.info("У пользователя {} есть навыки {}", userId, userSkills);
        return userSkills;
    }
}
