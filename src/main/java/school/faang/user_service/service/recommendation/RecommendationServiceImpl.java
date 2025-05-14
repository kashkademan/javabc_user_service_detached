package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {
        validateRecommendation(recommendation);

        Long recommendationId = recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
        if (recommendation.getSkillOffers() != null) {
            for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
                skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationId);
            }
        }
        Recommendation recommendationEntity = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new DataValidationException("Failed to retrieve the created recommendation"));
        checkAndAddSkillsGuarantees(recommendationEntity);

        return recommendationMapper.toDto(recommendationEntity);
    }

    @Override
    @Transactional
    public RecommendationDto update(RecommendationDto updated) {
        validateRecommendation(updated);

        recommendationRepository.update(
                updated.getAuthorId(),
                updated.getReceiverId(),
                updated.getContent());
        skillOfferRepository.deleteAllByRecommendationId(updated.getId());
        if (updated.getSkillOffers() != null) {
            for (SkillOfferDto skillOfferDto : updated.getSkillOffers()) {
                skillOfferRepository.create(skillOfferDto.getSkillId(), updated.getId());
            }
        }
        Recommendation recommendationEntity = recommendationRepository.findById(updated.getId())
                .orElseThrow(() -> new DataValidationException("Failed to retrieve the created recommendation"));
        checkAndAddSkillsGuarantees(recommendationEntity);

        return recommendationMapper.toDto(recommendationEntity);
    }

    @Override
    @Transactional
    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(0, Integer.MAX_VALUE)
        );

        return recommendationMapper.toDtoList(recommendationsPage.getContent());
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationsPage = recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(0, Integer.MAX_VALUE)
        );

        return recommendationMapper.toDtoList(recommendationsPage.getContent());
    }

    private void validateRecommendation(RecommendationDto recommendation) {
        checkLastRecommendationDate(recommendation);
        if (recommendation.getSkillOffers() != null && !recommendation.getSkillOffers().isEmpty()) {
            checkMissingSkills(recommendation);
            checkUniqueSkills(recommendation);
        }
    }

    private void checkLastRecommendationDate(RecommendationDto recommendation) {
        Optional<Recommendation> previousRecommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendation.getAuthorId(),
                        recommendation.getReceiverId());
        if (previousRecommendation.isPresent()) {
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            if (previousRecommendation.get().getCreatedAt().isAfter(sixMonthsAgo)) {
                throw new DataValidationException(
                        "You cannot give a recommendation to the same user more than once every 6 months");
            }
        }
    }

    private void checkMissingSkills(RecommendationDto recommendation) {
        List<Long> missingSkillIds = recommendation.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .filter(skillId -> !skillRepository.existsById(skillId))
                .toList();
        if (!missingSkillIds.isEmpty()) {
            if (missingSkillIds.size() == 1) {
                throw new DataValidationException(
                        String.format("Skill with id %d does not exist in the system", missingSkillIds.get(0))
                );
            } else {
                throw new DataValidationException(
                        String.format("The following skills do not exist in the system: %s",
                                missingSkillIds.stream()
                                        .map(String::valueOf)
                                        .collect(Collectors.joining(", ")))
                );
            }
        }
    }

    private void checkUniqueSkills(RecommendationDto recommendation) {
        Set<Long> uniqueSkillIds = new HashSet<>();
        for (SkillOfferDto skillOffer : recommendation.getSkillOffers()) {
            if (!uniqueSkillIds.add(skillOffer.getSkillId())) {
                throw new DataValidationException("There are duplicate skills in the recommendation");
            }
        }
    }

    private void checkAndAddSkillsGuarantees(Recommendation recommendation) {
        User author = recommendation.getAuthor();
        User receiver = recommendation.getReceiver();
        for (SkillOffer skillOffer : recommendation.getSkillOffers()) {
            Skill skill = skillOffer.getSkill();
            if (receiver.getSkills().contains(skill)) {
                boolean isAuthorAlreadyGuarantor = skill.getGuarantees().stream()
                        .anyMatch(guarantee -> guarantee.getGuarantor().equals(author) &&
                                guarantee.getUser().equals(receiver));

                if (!isAuthorAlreadyGuarantor) {
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