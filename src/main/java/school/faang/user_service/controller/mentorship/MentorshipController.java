package school.faang.user_service.controller.mentorship;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.GetMenteesResponseDto;
import school.faang.user_service.dto.mentorship.GetMentorsResponseDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<GetMenteesResponseDto> getMentees(@PathVariable
                                                  @Min(value = 1, message = "id must be a positive number")
                                                      Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<GetMentorsResponseDto> getMentors(@PathVariable
                                                  @Min(value = 1, message = "id must be a positive number")
                                                      Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    public void deleteMentee(@PathVariable @Min(value = 1, message = "id must be a positive number") Long menteeId,
                             @PathVariable @Min(value = 1, message = "id must be a positive number") Long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    public void deleteMentor(@PathVariable @Min(value = 1, message = "id must be a positive number") Long menteeId,
                             @PathVariable @Min(value = 1, message = "id must be a positive number") Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}