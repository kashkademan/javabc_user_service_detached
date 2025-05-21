package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.exception.PreConditionFailedException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;

import java.util.List;

import static school.faang.user_service.util.LogsConstants.CONDITION_FOR_OFFERS_AMOUNT_FAILED;
import static school.faang.user_service.util.LogsConstants.RECOMMENDATION_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.SKILL_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;
import static school.faang.user_service.util.SettingsConstants.MIN_SKILL_OFFERS;

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
        log.info("Create skill: {}", skill);
        skillValidator.validateTitleUnique(skill.getTitle());
        Skill createdSkill = skillRepository.save(skill);
        log.info("Created Skill: {}", createdSkill);
        return createdSkill;
    }

    @Transactional(readOnly = true)
    public List<Skill> getUserSkills(long userId) {
        List<Skill> userSkills = skillRepository.findAllByUserId(userId);
        log.info("User {} has skills {}", userId, userSkills);
        return userSkills;
    }

    @Transactional(readOnly = true)
    public List<Skill> getOfferedSkills(long userId) {
        List<Skill> offeredSkills = skillRepository.findSkillsOfferedToUser(userId);
        log.info("User {} has offered skills {}", userId, offeredSkills);
        return offeredSkills;
    }

    @Transactional
    public Skill acquireSkillFromOffers(long userId, long skillId) {
        log.info("acquireSkillFromOffers userId = {}, skillId = {}", userId, skillId);
        Skill skill = skillRepository.findById(skillId).
                orElseThrow(() -> new RecordNotFoundException(String.format(SKILL_NOT_FOUND, skillId)));
        User user = userRepository.findById(userId).
                orElseThrow(() -> new RecordNotFoundException(String.format(USER_NOT_FOUND, userId)));
        skillValidator.validateUserHasSkill(userId, skillId);
        List<SkillOffer> skillOfferList = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        log.info("acquireSkillFromOffers skillOfferList = {}", skillOfferList);
        int offeredAmount = skillOfferList.size();
        if (offeredAmount < MIN_SKILL_OFFERS) {
            log.error(CONDITION_FOR_OFFERS_AMOUNT_FAILED);
            throw new PreConditionFailedException(CONDITION_FOR_OFFERS_AMOUNT_FAILED);
        }
        skillRepository.assignSkillToUser(skillId, userId);
        skillOfferList.forEach((skillOffer) -> fillUserSkillGuarantee(skillOffer, user, skill));
        log.info("acquireSkillFromOffers skill = {}", skill);
        return skill;
    }

    private void fillUserSkillGuarantee(SkillOffer skillOffer, User user, Skill skill) {
        Recommendation recommendation = skillOffer.getRecommendation();
        if (recommendation == null) {
            log.error(RECOMMENDATION_NOT_FOUND);
            throw new RecordNotFoundException(RECOMMENDATION_NOT_FOUND);
        }
        UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                .user(user)
                .skill(skill)
                .guarantor(recommendation.getAuthor())
                .build();
        userSkillGuaranteeRepository.save(userSkillGuarantee);
    }
}
