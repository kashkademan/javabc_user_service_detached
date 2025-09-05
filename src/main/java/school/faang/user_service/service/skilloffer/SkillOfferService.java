package school.faang.user_service.service.skilloffer;


import school.faang.user_service.entity.recommendation.SkillOffer;

public interface SkillOfferService {
    SkillOffer create(long skillId, long recommendationId);
}
