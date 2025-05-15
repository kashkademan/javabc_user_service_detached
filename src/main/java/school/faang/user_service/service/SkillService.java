package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

import static school.faang.user_service.constant.Constants.MIN_SKILL_OFFERS;
import static school.faang.user_service.constant.Constants.SKILL_NOT_FOUND;
import static school.faang.user_service.constant.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillValidator skillValidator;

    @Transactional
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

    @Transactional(readOnly = true)
    public List<Skill> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        log.info("У пользователя {} есть предложенные навыки {}", userId, offeredSkills);
        return offeredSkills;
    }

    @Transactional
    public Skill acquireSkillFromOffers(long userId, long skillId) {
        Skill skill;
        skillValidator.validateUserHasSkill(userId, skillId);
        List<SkillOffer> skillOfferList = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        int offeredAmount = skillOfferList.size();
        if (offeredAmount >= MIN_SKILL_OFFERS) {
            skill = skillRepository.findById(skillId).
                    orElseThrow(() -> new EntityNotFoundException(String.format(SKILL_NOT_FOUND, skillId)));
            User user = userRepository.findById(userId).
                    orElseThrow(() -> new EntityNotFoundException(String.format(USER_NOT_FOUND, userId)));
            skillRepository.assignSkillToUser(skillId, userId);
            skillOfferList.forEach((skillOffer) -> {
                Recommendation recommendation = skillOffer.getRecommendation();
                if (recommendation != null) {
                    UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                            .user(user)
                            .skill(skill)
                            .guarantor(recommendation.getAuthor())
                            .build();
                    userSkillGuaranteeRepository.save(userSkillGuarantee);
                }
            });

        } else {
            skill = null;
        }
        return skill;
    }
}
