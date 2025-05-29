package school.faang.user_service.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/{userId}/mentees")
    public List<UserDto> getMentees(@PathVariable @Min(1) long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/{userId}/mentors")
    public List<UserDto> getMentor(@PathVariable @Min(1) long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentor/{mentorId}/delete")
    public ResponseEntity<Void> deleteMentor(
            @PathVariable @Min(1) long mentorId,
            @RequestParam @Min(1) long menteeId) {
        return mentorshipService.deleteMentor(menteeId, mentorId);
    }

    @DeleteMapping("/mentee/{menteeId}/delete")
    public ResponseEntity<Void> deleteMentee(
            @PathVariable @Min(1) long menteeId,
            @RequestParam @Min(1) long mentorId) {
        return mentorshipService.deleteMentee(menteeId, mentorId);
    }
}