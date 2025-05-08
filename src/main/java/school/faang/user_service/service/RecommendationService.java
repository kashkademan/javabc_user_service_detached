package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    @Autowired
    public RecommendationService(RecommendationRepository recommendationRepository, SkillOfferRepository skillOfferRepository,
                                 SkillRepository skillRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository,
                                 RecommendationMapper recommendationMapper) {
        this.recommendationRepository = recommendationRepository;
        this.skillOfferRepository = skillOfferRepository;
        this.skillRepository = skillRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.recommendationMapper = recommendationMapper;
    }

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);
        Long recommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationId);
        }
        Recommendation recommendationEntity = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Не удалось получить созданную рекомендацию"));
        checkAndAddSkillsGuarantees(recommendationEntity);

        return recommendationMapper.toDto(recommendationEntity);
    }

    @Transactional
    public RecommendationDto update(RecommendationDto updated) {
        validateRecommendation(updated);

        recommendationRepository.update(
                updated.getAuthorId(),
                updated.getReceiverId(),
                updated.getContent());
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());
        for (SkillOfferDto skillOfferDto : updated.getSkillOffers()) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), updated.getId());
        }
        Recommendation recommendationEntity = recommendationRepository.findById(updated.getId())
                .orElseThrow(() -> new DataValidationException("Не удалось получить созданную рекомендацию"));
        checkAndAddSkillsGuarantees(recommendationEntity);

        return recommendationMapper.toDto(recommendationEntity);
    }

    @Transactional
    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(0, Integer.MAX_VALUE)
        );

        return recommendationMapper.toDtoList(recommendationsPage.getContent());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        Optional<Recommendation> previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(), recommendation.getReceiverId());
        if (previousRecommendation.isPresent()) {
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            if (previousRecommendation.get().getCreatedAt().isAfter(sixMonthsAgo)) {
                throw new DataValidationException("Нельзя давать рекомендацию одному пользователю чаще, чем раз в 6 месяцев");
            }
        }

        if (recommendation.getSkillOffers() != null && !recommendation.getSkillOffers().isEmpty()) {
            recommendation.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillId)
                    .forEach(skillId -> {
                        if (!skillRepository.existsById(skillId)) {
                            throw new DataValidationException(String.format("Навык с id %d отсутствует в системе", skillId));
                        }
                    });

            Set<Long> uniqueSkillIds = new HashSet<>();
            for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
                if (!uniqueSkillIds.add(skillOffer.getSkillId())) {
                    throw new DataValidationException("В рекомендации есть дублирующиеся навыки");
                }
            }
        }
    }

    private void checkAndAddSkillsGuarantees(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            Skill skill = skillOffer.getSkill();
            if (receiver.getSkills().contains(skill)) {
                boolean authorAlreadyGuarantor = skill.getGuarantees().stream()
                        .anyMatch(guarantee -> guarantee.getGuarantor().equals(author) &&
                                guarantee.getUser().equals(receiver));

                if (!authorAlreadyGuarantor) {
                    UserSkillGuarantee newGuarantee = UserSkillGuarantee.builder()
                            .user(receiver)
                            .skill(skill)
                            .guarantor(author)
                            .build();
                    skill.getGuarantees().add(newGuarantee);
                    userSkillGuaranteeRepository.save(newGuarantee);
                }
            }
        }
    }
}