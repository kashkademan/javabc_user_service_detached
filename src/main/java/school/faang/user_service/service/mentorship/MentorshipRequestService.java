package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;

import java.util.List;

public interface MentorshipRequestService {
    MentorshipRequestDto create(CreateMentorshipRequestDto requestDto);

    List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter);

    MentorshipRequestDto accept(long requestId);

    MentorshipRequestDto reject(long requestId, RejectionDto rejectionDto);
}
