package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;

import java.util.List;

@Transactional(readOnly = true)
public interface MentorshipRequestService {
    @Transactional
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    @Transactional
    MentorshipRequestDto acceptRequest(Long id);

    @Transactional
    MentorshipRequestDto rejectRequest(Long id, RejectionDto rejectionDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filter);
}