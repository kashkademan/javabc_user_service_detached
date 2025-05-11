package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    MentorshipRequestDto acceptRequest(Long id);

    MentorshipRequestDto rejectRequest(Long id, RejectionDto rejectionDto);

    List<MentorshipRequestDto> getRequests(RequestFilterDto filter);
}