package school.faang.user_service.repository.mentorship.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.repository.mentorship.dto.CreateMentorshipRequest;
import school.faang.user_service.repository.mentorship.service.MentorshipService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @PostMapping
    public ResponseEntity addMentorship(@Valid @RequestBody CreateMentorshipRequest request) {
        mentorshipService.addMentorship(request.getMentorId(), request.getMenteeId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Mentorship created successfully");
    }

    @GetMapping("/users/{userId}/mentees")
    public ResponseEntity<List<UserDto>> getMentees(
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId) {
        List<UserDto> mentees = mentorshipService.getMentees(userId);
        return ResponseEntity.ok(mentees);
    }

    @GetMapping("/users/{userId}/mentors")
    public ResponseEntity<List<UserDto>> getMentors(
            @PathVariable @Min(value = 1, message = "User ID must be positive") long userId) {
        List<UserDto> mentors = mentorshipService.getMentors(userId);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public ResponseEntity deleteMentorship(@Valid @RequestBody CreateMentorshipRequest request) {
        mentorshipService.deleteMentorship(request.getMenteeId(), request.getMentorId());
        return ResponseEntity.ok("Mentorship deleted successfully");
    }
}
