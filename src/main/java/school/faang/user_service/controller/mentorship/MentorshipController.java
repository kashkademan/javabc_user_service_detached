package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorships")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping("/{mentorId}/{menteeId}")
    UserDto addMentorship(@PathVariable long mentorId, @PathVariable long menteeId) {
        return mentorshipService.addMentorship(mentorId, menteeId);
    }

    @GetMapping("/{userId}/mentees")
    List<UserDto> getMentees(@PathVariable long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/{menteeId}/{mentorId}")
    UserDto deleteMentorship(@PathVariable long menteeId, @PathVariable long mentorId) {
        return mentorshipService.deleteMentorship(menteeId, mentorId);
    }

}
