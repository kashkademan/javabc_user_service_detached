package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterStrategy;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final List<RecommendationRequestFilterStrategy> recommendationRequestFilters;

    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {
        User requester = userRepository.findById(recommendationRequestDto.getRequesterId())
                .orElseThrow(() -> new IllegalArgumentException("Requester with id %s was not found".formatted(recommendationRequestDto.getRequesterId())));
        User receiver = userRepository.findById(recommendationRequestDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver with id %s was not found".formatted(recommendationRequestDto.getReceiverId())));

        RecommendationRequest recommendationRequest = recommendationRequestRepository.findLatestPendingRequest(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .orElse(recommendationRequestMapper.toEntity(recommendationRequestDto));

        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);

        if (!recommendationRequest.getUpdatedAt().isBefore(LocalDateTime.now().minus(Period.ofMonths(6)))) {
            throw new IllegalArgumentException("Recommendation request has already been updated in the last 6 months.");
        }

        if (!recommendationRequestDto.getSkillIds().stream().allMatch(skillRepository::existsById)) {
            throw new NoSuchElementException("Not all required skills exist in data base");
        }

        recommendationRequest.setSkills(
                recommendationRequestDto.getSkillIds()
                        .stream()
                        .map(skillRepository::findById)
                        .map(optionalSkill ->
                                skillRequestRepository.create(
                                        recommendationRequest.getId(),
                                        optionalSkill.get().getId()))
                        .toList());

        RecommendationRequest savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequestMapper.toDto(savedRecommendationRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        Stream<RecommendationRequest> recommendations = recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilterStrategy recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(filter)) {
                recommendations = recommendationRequestFilter.apply(recommendations, filter);
            }
        }

        return recommendations
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        return recommendationRequestMapper.toDto(recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recommendation request with id %s doesn't exist".formatted(id))));
    }


    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recommendation request with id %s doesn't exist".formatted(id)));

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Unable to reject request");
        }

        // тут не сохраняю в БД значит метод не меняет ни как логику, можно обновить страницу и всё будет по старому
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getReason());

        return recommendationRequestMapper.toDto(recommendationRequest);
    }
}
