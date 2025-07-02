package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<SkillOffer> findAllOffersOfSkill(long skillId) {
        long userId = userContext.getUserId();
        return skillOfferRepository.findAllOffersOfSkill(userId, skillId);
    }
}
