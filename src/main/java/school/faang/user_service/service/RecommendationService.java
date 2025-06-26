package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private static final int RECOMMENDATIONS_PER_PAGE = 100;
    private static final Sort PAGE_SORT = Sort.by("updated_at").descending();

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillOfferMapper skillOfferMapper;
    private final SkillRepository skillRepository;
    private final RecommendationMapper recommendationMapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        validation(recommendationDto);
        recommendationDto.skillOffers().forEach(skillOfferDto -> {
            checkForGuarantee(recommendationMapper.toEntity(recommendationDto), skillOfferDto);
            skillOfferRepository.save(skillOfferMapper.toEntity(skillOfferDto));
        });

        Long id = recommendationRepository
                .create(recommendationDto.authorId(), recommendationDto.receiverId(), recommendationDto.content());

        return recommendationMapper.toDto(recommendationRepository.findById(id).get());
    }

    @Transactional
    public RecommendationDto update(RecommendationDto recommendationDto) {
        validation(recommendationDto);
        recommendationRepository
                .update(recommendationDto.authorId(), recommendationDto.receiverId(), recommendationDto.content());
        skillOfferRepository.deleteAllByRecommendationId(recommendationDto.id());
        recommendationDto.skillOffers()
                .forEach(skillOfferDto -> {
                    skillOfferRepository.create(skillOfferDto.skillId(), recommendationDto.id());
                    checkForGuarantee(recommendationMapper.toEntity(recommendationDto), skillOfferDto);
                });

        return recommendationMapper.toDto(recommendationRepository.findById(recommendationDto.id()).get());
    }

    @Transactional
    public void delete(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Nonexistent id");
        }

        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId, int page) {
        Pageable pageable = PageRequest.of(page, RECOMMENDATIONS_PER_PAGE, PAGE_SORT);
        Page<Recommendation> userRecommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return userRecommendations.getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long id, int page) {
        Pageable pageable = PageRequest.of(page, RECOMMENDATIONS_PER_PAGE, PAGE_SORT);
        Page<Recommendation> givenRecommendations = recommendationRepository.findAllByAuthorId(id, pageable);
        if (givenRecommendations.isEmpty()) {
            throw new IllegalArgumentException("Nonexistent id");
        }

        return givenRecommendations
                .getContent()
                .stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private void checkForGuarantee(Recommendation recommendation, SkillOfferDto skillOfferDto) {
        if (recommendation.getReceiver().getSkills().contains(skillOfferMapper.toEntity(skillOfferDto).getSkill())) {
            /*List<UserSkillGuarantee> userSkillGuarantees =
                    skillOfferMapper.toEntity(skillOfferDto).getSkill().getGuarantees();
            userSkillGuarantees.add(new UserSkillGuarantee(null,
                    recommendation.getReceiver(),
                    skillOfferMapper.toEntity(skillOfferDto).getSkill(),
                    recommendation.getAuthor()));

            skillOfferMapper.toEntity(skillOfferDto).getSkill().setGuarantees(userSkillGuarantees);*/
        }
    }


    private boolean isAfterSixMonth(RecommendationDto recommendationDto) {
        return recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                        recommendationDto.authorId(),
                        recommendationDto.receiverId()
                )
                .filter(r -> r.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(6)))
                .isPresent();
    }

    private boolean ifSkillsExist(RecommendationDto recommendationDto) {
        return recommendationDto.skillOffers().size() != skillRepository.countExisting(recommendationDto.skillOffers()
                .stream()
                .map(SkillOfferDto::skillId)
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
