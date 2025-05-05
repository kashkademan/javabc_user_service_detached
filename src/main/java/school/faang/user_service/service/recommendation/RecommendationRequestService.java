package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.Validator;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    public static final String SIX_MONTHS_PERIOD_ERROR = "A recommendation request from the same user " +
            "to another can be sent no more than once every 6 months.";
    public static final String REQUEST_BY_ID_NOT_FOUND = "The recommendation request by id={0} was not found.";
    public static final String SKILLS_MISSING_FROM_DATABASE = "one or more skills are missing from the database";

    private final UserService userService;
    private final SkillService skillService;

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestMapper mapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;
    //todo: Validator сделать возможность сортировки, задать порядок валидирования.
    private final List<Validator<RecommendationRequestDto>> requestValidators;
    private final List<Validator<RejectionDto>> rejectValidators;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        log.info("validators count: {}", requestValidators.size());
        requestValidators.forEach(validator -> {
            log.info("run validator: {}", validator.getClass().getName());
            validator.validate(dto);
        });
        validateTimePeriod(dto);
        RecommendationRequest entity = mapper.toEntity(dto);
        fillEntity(entity, dto);
        RecommendationRequest resultEntity = requestRepository.save(entity);
        return mapper.toDto(resultEntity);
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> requests = requestRepository.findAll().stream();
        filters.forEach(filter -> {
            log.info("run filter {}", filter.getClass().getName());
            if (filter.isApplicable(filterDto)) {
                filter.apply(requests, filterDto);
            }
        });

        return requests
                .map(entity -> {
                    RecommendationRequestDto dto = mapper.toDto(entity);
                    setDtoSkills(entity, dto);
                    dto.setRequesterId(entity.getRequester().getId());
                    dto.setReceiverId(entity.getReceiver().getId());
                    return dto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));

        RecommendationRequestDto dto = mapper.toDto(entity);
        setDtoSkills(entity, dto);
        dto.setRequesterId(entity.getRequester().getId());
        dto.setReceiverId(entity.getReceiver().getId());
        return dto;
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        rejectValidators.forEach(validator -> validator.validate(rejection));
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));
        log.info("before {}", entity);
        Set<RequestStatus> checkStatusForReject = Set.of(RequestStatus.REJECTED, RequestStatus.ACCEPTED);
        if (checkStatusForReject.contains(entity.getStatus())) {
            //todo: сделать универсальным поднятие ошибок
            throw requestException(MessageFormat.format(
                    "The recommendation request status cannot be changed. " +
                            "Entity (id={0}) have one of the next status {1}",
                    id, checkStatusForReject));
        }
        Integer rowCount = requestRepository.setStatus(id, RequestStatus.REJECTED, rejection.reason());
        if (rowCount != 1) {
            throw requestException(MessageFormat.format(
                    "The status of the recommendation request has not been changed (id={0})", id));
        }
        RecommendationRequest result = requestRepository.findById(id)
                .orElseThrow(() -> requestException(id, REQUEST_BY_ID_NOT_FOUND));
        log.info("after update {}", result);
        return mapper.toDto(result);
    }

    /**
     * Метод проверяет, что запрос рекомендации от одного и того же пользователя к другому можно
     * отправлять не чаще, чем один раз в 6 месяцев.
     */
    private void validateTimePeriod(RecommendationRequestDto dto) {
        int requestCount = requestRepository.countRepeatedRequest(dto.getRequesterId(), dto.getReceiverId());
        if (requestCount > 0) {
            throw requestException(SIX_MONTHS_PERIOD_ERROR);
        }
    }

    @NotNull
    private static RecommendationRequestException requestException(Long id, String message) {
        String errorMessage = MessageFormat.format(message, id);
        log.error(errorMessage);
        return new RecommendationRequestException(errorMessage);
    }

    @NotNull
    private static RecommendationRequestException requestException(String message) {
        log.error(message);
        return new RecommendationRequestException(message);
    }

    private void fillEntity(RecommendationRequest entity, RecommendationRequestDto dto) {
        //REQUESTER. Если нет в БД, то ошибка
        entity.setRequester(getUser(dto.getRequesterId()));
        //RECEIVER. Если нет в БД, то ошибка
        entity.setReceiver(getUser(dto.getReceiverId()));
        entity.setStatus(getStatus(dto));
        List<Skill> skills = getSkills(dto);
        skills.forEach(skill -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(entity);
            skillRequest.setSkill(skill);
            entity.addSkillRequest(skillRequest);
        });
    }

    private RequestStatus getStatus(RecommendationRequestDto dto) {
        if (dto.getStatus() == null || dto.getStatus().isBlank()) {
            return RequestStatus.PENDING;
        } else {
            return RequestStatus.valueOf(dto.getStatus());
        }
    }

    private User getUser(Long userId) {
        return userService.getUserById(userId);
    }

    private List<Skill> getSkills(RecommendationRequestDto dto) {
        List<Skill> skills = skillService.getSkillsByIds(dto.getSkills());
        if (skills.size() != dto.getSkills().size()) {
            throw new RecommendationRequestException(SKILLS_MISSING_FROM_DATABASE);
        }
        return skills;
    }

    private void setDtoSkills(RecommendationRequest entity, RecommendationRequestDto dto) {
        entity.getSkills().forEach(skillRequest -> dto.addSkill(skillRequest.getId()));
    }

}
