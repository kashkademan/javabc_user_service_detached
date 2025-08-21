package school.faang.user_service.service.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.property.RecommendationRequestProperty;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.event.RecommendationRequestEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserContext userContext;
    private final SkillRequestRepository skillRequestRepository;
    private final Set<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationRequestProperty property;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto) {
        long requesterId = userContext.getUserId();
        long receiverId = recommendationDto.receiverId();

        if (requesterId == receiverId) {
            throw new DataValidationException("You cannot send recommendation to yourself");
        }

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);

        if (latestPendingRequest.isPresent() && latestPendingRequest.get().getCreatedAt()
                .plus(property.quantity(), property.period()).isAfter(LocalDateTime.now())) {
            throw new DataValidationException("You can send recommendation request once every "
                    + property.quantity() + " " + property.period().toString().toLowerCase());
        }

        RecommendationRequest recommendationRequest = recommendationRequestMapper
                .toRecommendationRequest(recommendationDto);
        recommendationRequest.setRequester(userRepository.getByIdOrThrow(requesterId));
        recommendationRequest.setReceiver(userRepository.getByIdOrThrow(receiverId));
        recommendationRequest.setStatus(RequestStatus.PENDING);

        if (recommendationDto.skillIds() != null) {
            recommendationRequest.setSkills(new ArrayList<>());
        }

        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        if (recommendationDto.skillIds() != null) {
            for (long skillId : recommendationDto.skillIds()) {
                SkillRequest skillRequest = skillRequestRepository
                        .create(recommendationRequest.getId(), skillId);
                recommendationRequest.addSkillRequest(skillRequest);
            }
        }

        log.info("Recommendation request created: {}", recommendationRequest.getId());

        RecommendationRequestEvent event = new RecommendationRequestEvent(
                recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId(),
                recommendationRequest.getId()
        );

        try {
            String jsonString = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("recommendation-request-topic", jsonString);
        } catch (JsonProcessingException e) {
            log.error("Error serializing RecommendationRequestEvent to JSON: {}", e.getMessage());
        }

        return recommendationRequestMapper.toRecommendationRequestDto(recommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters) {
        Stream<RecommendationRequest> recommendationRequestStream =
                recommendationRequestRepository.findAll().stream();

        for (RecommendationRequestFilter recommendationRequestFilter : recommendationRequestFilters) {
            if (recommendationRequestFilter.isApplicable(filters)) {
                recommendationRequestStream = recommendationRequestFilter
                        .apply(recommendationRequestStream, filters);
            }
        }

        return recommendationRequestStream
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getById(long id) {
        RecommendationRequest recommendationRequest =
                recommendationRequestRepository.getByIdOrThrow(id);
        log.info("Recommendation request found: {}", recommendationRequest.getId());
        return recommendationRequestMapper.toRecommendationRequestDto(recommendationRequest);
    }

    @Override
    public void accept(long id) {
        long receiverId = userContext.getUserId();

        if (receiverId != recommendationRequestRepository.getByIdOrThrow(id).getReceiver().getId()) {
            throw new ForbiddenException("You cannot accept this recommendation request");
        }

        if (recommendationRequestRepository.getByIdOrThrow(id).getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("You cannot accept this recommendation request"
                    + " because it is not in PENDING status");
        }

        RecommendationRequest recommendationRequest = recommendationRequestRepository.getByIdOrThrow(id);
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(recommendationRequest);

        log.info("Recommendation request accepted: {}", recommendationRequest.getId());
    }

    @Override
    public void reject(long id, RejectionDto rejection) {
        long receiverId = userContext.getUserId();

        if (receiverId != recommendationRequestRepository.getByIdOrThrow(id).getReceiver().getId()) {
            throw new ForbiddenException("You cannot reject this recommendation request");
        }

        if (recommendationRequestRepository.getByIdOrThrow(id).getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("You cannot reject this recommendation request"
                    + " because it is not in PENDING status");
        }

        RecommendationRequest recommendationRequest = recommendationRequestRepository.getByIdOrThrow(id);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
        recommendationRequestRepository.save(recommendationRequest);

        log.info("Recommendation request rejected: {}", recommendationRequest.getId());
    }
}
