package school.faang.user_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

@Service
@RequiredArgsConstructor
public class SkillOfferService {
    private final SkillOfferRepository skillOfferRepository;

    public List<SkillOffer> findAllOffersOfSkill(long skillId, long userId) {
        return skillOfferRepository.findAllOffersOfSkill(skillId, userId);
    }
}
