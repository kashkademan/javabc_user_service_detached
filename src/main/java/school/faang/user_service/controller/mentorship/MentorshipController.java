package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.dto.mentorship.MentorDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentee-of-user/{userId}")
    public List<MenteeDto> getMentees(@PathVariable @NotNull @Positive Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentor-of-user/{userId}")
    public List<MentorDto> getMentors(@PathVariable @NotNull @Positive Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentee/{menteeId}/forMentor/{mentorId}")
    public ResponseEntity<Void> deleteMentee(
            @PathVariable @NotNull @Positive Long menteeId,
            @PathVariable @NotNull @Positive Long mentorId
    ) {
        mentorshipService.deleteMentorship(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mentor/{mentorId}/forMentee/{menteeId}")
    public ResponseEntity<Void> deleteMentor(
            @PathVariable @NotNull @Positive Long mentorId,
            @PathVariable @NotNull @Positive Long menteeId
    ) {
        mentorshipService.deleteMentorship(mentorId, menteeId);
        return ResponseEntity.noContent().build();
    }
}