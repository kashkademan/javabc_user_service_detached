package school.faang.user_service.repository.mentorship.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.repository.mentorship.service.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
@Component
@Controller
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public void addMentorship(long mentorId, long menteeId) {
        mentorshipService.addMentorship(mentorId, menteeId);
    }

    public List<UserDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<UserDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentorship(long menteeId, long mentorId) {
        mentorshipService.deleteMentorship(menteeId, mentorId);
    }
}
