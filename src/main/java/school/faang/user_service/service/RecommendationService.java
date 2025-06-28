package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.builder.NotificationEventBuilder;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.NotificationEventType;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.Recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    public static final int MONTHS_LAST_RECOMMENDATION = 6;
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationValidator recommendationValidator;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final NotificationEventBuilder eventBuilder;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendation) {

        userValidator.validatorUserExistence(recommendation.getReceiverId());
        recommendationValidator.validatorNullSKillOfferTru(recommendation.getSkillOffers());
        checkValidDateRecommendation(recommendation);
        checkGeneralRecommendationSkillOffer(recommendation);

        long newRecommendationId = saveCreateRecommendation(recommendation);
        saveOrCreateSkillOffers(recommendation, newRecommendationId);

        eventBuilder.createPublish(recommendation.getReceiverId(), NotificationEventType.CREATE_RECOMMENDATION);
        return recommendationMapper.toDto(recommendationRepository.findById(newRecommendationId)
                .orElseThrow(() -> new IllegalStateException("Ошибка сохранения рекомендации!")));
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        userValidator.validatorUserExistence(recommendation.getReceiverId());
        checkValidDateRecommendation(recommendation);
        checkGeneralRecommendationSkillOffer(recommendation);

        recommendationRepository.update(recommendation.getAuthorId(), recommendation.getReceiverId(), recommendation.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());

        saveOrCreateSkillOffers(recommendation, recommendation.getId());

        return recommendationMapper.toDto(recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> new IllegalStateException("Ошибка обновления рекомендации!")));
    }

    public boolean delete(long id) {
        recommendationValidator.validatorExistenceRecommendation(id);

        recommendationRepository.deleteById(id);
        return !recommendationRepository.existsById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long id, int page, int size) {

        userValidator.validatorUserExistence(id);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(id, pageable);

        return recommendationPage.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long id, int page, int size) {
        userValidator.validatorUserExistence(id);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(id, pageable);

        return recommendationPage.map(recommendationMapper::toDto);
    }

    private void saveOrCreateSkillOffers(RecommendationDto recommendation, long newRecommendationId) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();

        for (SkillOfferDto skillOffer : skillOffers) {
            long skillId = skillOffer.getSkillId();
            if (skillRepository.findUserSkill(skillId, recommendation.getReceiverId()).isPresent()) {
                saveSkillGuarantees(recommendation, skillId);
            } else {
                skillOfferRepository.create(skillId, newRecommendationId);
            }
        }
    }

    private void checkGeneralRecommendationSkillOffer(RecommendationDto recommendation) {
        List<SkillOfferDto> skillOffers = recommendation.getSkillOffers();

        if (skillOffers != null && skillOffers.isEmpty()) {
            List<Long> skillIds = recommendation.getSkillOffers().stream()
                    .map(SkillOfferDto::getSkillId)
                    .toList();

            recommendationValidator.validatorIdDuplicates(skillIds);
            skillValidator.validatorExistenceSkillListSkillOffer(skillIds);
            recommendationValidator.validatorExistenceUserSkillGuarantee(recommendation, skillIds);
        }
    }

    private void checkValidDateRecommendation(RecommendationDto recommendation) {
        LocalDateTime recommendationThresholdDate = LocalDateTime.now().minusMonths(MONTHS_LAST_RECOMMENDATION);
        Optional<Recommendation> optionalRecommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendation.getAuthorId(), recommendation.getReceiverId());
        optionalRecommendation.ifPresent(options
                -> recommendationValidator.validateRecommendationDate(options.getCreatedAt(), recommendationThresholdDate));
    }

    private void saveSkillGuarantees(RecommendationDto recommendationDto, long skillId) {
        User user = userRepository.getReferenceById(recommendationDto.getReceiverId());
        User guarantor = userRepository.getReferenceById(recommendationDto.getAuthorId());
        Skill skill = skillRepository.getReferenceById(skillId);
        UserSkillGuarantee guarantees = UserSkillGuarantee.builder()
                .user(user)
                .skill(skill)
                .guarantor(guarantor)
                .build();

        userSkillGuaranteeRepository.save(guarantees);
    }

    private long saveCreateRecommendation(RecommendationDto recommendation) {
        return recommendationRepository.create(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent());
    }
}