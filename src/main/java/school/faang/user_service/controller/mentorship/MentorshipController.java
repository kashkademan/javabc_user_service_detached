package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentee/{userId}")
    public List<MenteeDto> getMentees(@PathVariable Long userId) {
        validateId(userId);
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentor/{userId}")
    public List<MentorDto> getMentors(@PathVariable Long userId) {
        validateId(userId);
        return mentorshipService.getMentors(userId);
    }

    private void validateId(Long userId) {
        if (userId == null || userId < 0) {
            throw new DataValidationException("Invalid user id.");
        }
    }
}