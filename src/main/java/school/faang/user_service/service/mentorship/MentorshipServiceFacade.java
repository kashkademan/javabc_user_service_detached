package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceFacade {
    private final MentorshipService mentorshipService;

    public List<MenteeDto> getMentees(long mentorId) {
        List<User> mentees = mentorshipService.getMentees(mentorId);
        return mentees.stream()
                .map(MentorshipMapper::toMenteeDto)
                .toList();
    }

    public List<MentorDto> getMentors(long menteeId) {
        List<User> mentors = mentorshipService.getMentors(menteeId);
        return mentors.stream()
                .map(MentorshipMapper::toMentorDto)
                .toList();
    }
}

