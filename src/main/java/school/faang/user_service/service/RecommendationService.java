package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;
    private final Pageable pageable;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) throws DataValidationException {
        validation(recommendationDto);
        recommendationDto.getSkillOffers().forEach(skillOfferDto -> {
            checkForGuarantee(recommendationMapper.toEntity(recommendationDto), skillOfferDto);
            skillOfferRepository.save(skillOfferMapper.toEntity(skillOfferDto));
        });

        recommendationRepository
                .create(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        return recommendationDto;
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validation(recommendationDto);
        recommendationRepository.update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent());
        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.getId());
        recommendationDto.getSkillOffers()
                .forEach(skillOfferDto -> skillOfferRepository.create(skillOfferDto.getSkillId(), recommendationDto.getId()));
        recommendationDto.getSkillOffers()
                .forEach(skillOfferDto -> checkForGuarantee(recommendationMapper.toEntity(recommendationDto), skillOfferDto));

        return recommendationDto;
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationRepository.findAllByReceiverId(receiverId, pageable).getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long id) {
        return recommendationRepository.findAllByAuthorId(id, pageable).getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void checkForGuarantee(Recommendation recommendation, SkillOfferDto skillOfferDto) {
        if (recommendation.getReceiver().getSkills().contains(skillOfferMapper.toEntity(skillOfferDto).getSkill())) {
            List<UserSkillGuarantee> userSkillGuarantees = skillOfferMapper.toEntity(skillOfferDto).getSkill().getGuarantees();
            userSkillGuarantees.add(new UserSkillGuarantee(null,
                    recommendation.getReceiver(),
                    skillOfferMapper.toEntity(skillOfferDto).getSkill(),
                    recommendation.getAuthor()));

            skillOfferMapper.toEntity(skillOfferDto).getSkill().setGuarantees(userSkillGuarantees);
        }
    }


    private boolean isAfterSixMonth (RecommendationDto recommendationDto) {
        return recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()
                )
                .filter(r -> r.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
                .isPresent();
    }

    private boolean ifSkillsExist (RecommendationDto recommendationDto) {
        return recommendationDto.getSkillOffers().size() != skillRepository.countExisting(recommendationDto.getSkillOffers()
                .stream()
                .map(SkillOfferDto::getSkillId)
                .toList());
    }

    private void validation(RecommendationDto recommendationDto) {
        if (isAfterSixMonth(recommendationDto)) {
            throw new DataValidationException("Recommendation was created too soon after previous");
        }
        if (ifSkillsExist(recommendationDto)) {
            throw new DataValidationException("Some skills do not exist in our system");
        }
    }
}
