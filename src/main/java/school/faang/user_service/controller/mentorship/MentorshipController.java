package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipDto;
import school.faang.user_service.dto.mentorship.MentorshipDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/mentorships")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipDto addMentorship(@Valid @RequestBody CreateMentorshipDto request) {
        return mentorshipService.addMentorship(request.mentorId(), request.menteeId());
    }

    @GetMapping("/users/{userId}/mentees")
    public List<UserDto> getMentees(
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/users/{userId}/mentors")
    public List<UserDto> getMentors(
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String deleteMentorship(@RequestParam long mentorId, @RequestParam long menteeId) {
        mentorshipService.deleteMentorship(menteeId, mentorId);
        return "Mentorship deleted successfully";
    }
}
