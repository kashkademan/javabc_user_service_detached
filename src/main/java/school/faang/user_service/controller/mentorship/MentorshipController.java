package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentorship/mentees/{userId}")
    public ResponseEntity<List<MenteeDto>> getMentees(@PathVariable long userId) {
        List<MenteeDto> mentees = mentorshipService.getMentees(userId);
        return ResponseEntity.ok(mentees);
    }

    @GetMapping("/mentorship/mentors/{userId}")
    public ResponseEntity<List<MentorDto>> getMentors(@PathVariable long userId) {
        List<MentorDto> mentors = mentorshipService.getMentors(userId);
        return ResponseEntity.ok(mentors);
    }

    @DeleteMapping("/mentorship/{mentorId}/{menteeId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable long mentorId,
                                             @PathVariable long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mentorship/{menteeId}/{mentorId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable long mentorId,
                                             @PathVariable long menteeId) {
        mentorshipService.deleteMentor(mentorId, menteeId);
        return ResponseEntity.ok().build();
    }
}
