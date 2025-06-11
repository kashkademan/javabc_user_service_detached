package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.specification.MentorshipRequestSpecification;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.UserValidator;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestService {

    public static final int MONTHS_REQUEST_SENDING_RESTRICTIONS = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;


    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        mentorshipRequestValidator.validateDescriptionIsNotBlank(mentorshipRequestDto.getDescription());
        userValidator.validateNotSameUser(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        userValidator.validatorUserExistence(mentorshipRequestDto.getRequesterId());
        userValidator.validatorUserExistence(mentorshipRequestDto.getReceiverId());
        mentorshipRequestValidator.validateMentorshipRequestCooldown(mentorshipRequestDto);

        MentorshipRequest result = mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(result);
    }

    public Page<MentorshipRequestDto> getRequests(RequestFilterDto filter, Pageable pageable) {
        Specification<MentorshipRequest> spec = MentorshipRequestSpecification.buildFilter(filter);

        return mentorshipRequestRepository.findAll(spec, pageable)
                .map(mentorshipRequestMapper::toDto);
    }


    @Transactional
    public MentorshipRequestDto acceptRequest(long requestId) {

        MentorshipRequest request = getMentorshipRequest(requestId);
        mentorshipRequestValidator.validateNoAcceptedStatus(request.getStatus());

        User mentor = fetchUserOrThrow(request.getReceiver().getId());
        User mentee = fetchUserOrThrow(request.getRequester().getId());

        mentorshipRequestValidator.validateNotAlreadyMentor(mentor.getMentees(), mentee.getId());

        request.setStatus(RequestStatus.ACCEPTED);
        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        userRepository.save(mentor);
        userRepository.save(mentee);
        MentorshipRequest result = mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(result);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long requestId, RejectionDto rejection) {

        MentorshipRequest request = getMentorshipRequest(requestId);
        mentorshipRequestValidator.validateStatusNoReject(request.getStatus());

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());

        MentorshipRequest result = mentorshipRequestRepository.save(request);
        return mentorshipRequestMapper.toDto(result);
    }

    private MentorshipRequest getMentorshipRequest(long requestId) {
        return mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new DataValidationException("Запроса с таким id не существует!"));
    }

    private User fetchUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Ошибка, попробуйте еще раз!"));
    }
}