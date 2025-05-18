package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;

import java.util.List;

public interface MentorshipService {
    List<MenteeDto> getMentees(long userId);

    List<MentorDto> getMentors(long userId);

    void deleteMentorship(long mentorId, long menteeId);
}