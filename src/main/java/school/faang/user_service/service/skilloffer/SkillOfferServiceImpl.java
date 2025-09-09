package school.faang.user_service.service.skilloffer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.event.SkillOfferEvent;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.SkillOfferException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.SkillRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillOfferServiceImpl implements SkillOfferService {

    private final RecommendationRepository recommendationRepository;
    private final UserContext userContext;
    private final SkillRepository skillRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public SkillOffer create(long skillId, long recommendationId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new SkillOfferException("Skill not found"));
        Recommendation recommendation
                = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new SkillOfferException("Recommendation not found"));

        SkillOffer skillOffer = SkillOffer.builder()
                .skill(skill)
                .recommendation(recommendation)
                .build();

        SkillOfferEvent skillOfferEvent = SkillOfferEvent.builder()
                .requesterId(userContext.getUserId())
                .receiverId(recommendation.getReceiver().getId())
                .skillOfferId(skillId)
                .build();

        try {
            String event = objectMapper.writeValueAsString(skillOfferEvent);
            kafkaTemplate.send("skill-offer-event-topic", event);
        } catch (JsonProcessingException e) {
            log.error("Error serializing RecommendationRequestEvent to JSON: {}", e.getMessage());
        }

        return skillOffer;
    }
}
