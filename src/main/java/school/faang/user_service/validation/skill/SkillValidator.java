package school.faang.user_service.validation.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.SkillAlreadyExistsException;
import school.faang.user_service.exception.skill_offer.NotEnoughSkillOffersException;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillValidator {
    private static final int MIN_SKILL_OFFERS = 3;

    private SkillRepository skillRepository;

    public void checkSkillTitleIsUnique(String title) {
        if (skillRepository.existsByTitle(title)) {
            throw new SkillAlreadyExistsException(String.format("Skill with title = %s already exists", title));
        }
    }

    public void checkEnoughOffersToAcquireSkill(List<SkillOffer> offers) {
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new NotEnoughSkillOffersException("Not enough offers to acquire this skill");
        }
    }
}
