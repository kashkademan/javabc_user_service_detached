package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.mentorship.MentorshipDtoRequest;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequestMapping(value = "api/mentorship", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/add")
    public void addMentorship(@RequestBody @Valid MentorshipDtoRequest mentorshipDto) {
        mentorshipService.addMentorship(mentorshipDto.getMentorId(), mentorshipDto.getMenteeId());
    }

    @GetMapping("/mentees/{userId}")
    public List<UserDto> getMentees(@PathVariable Long userId) {
        return mentorshipService.getMentees(userId);
    }

    @GetMapping("/mentors/{userId}")
    public List<UserDto> getMentors(@PathVariable Long userId) {
        return mentorshipService.getMentors(userId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/remove")
    public void deleteMentorship(@RequestBody @Valid MentorshipDtoRequest mentorshipDto) {
        mentorshipService.deleteMentorship(mentorshipDto.getMentorId(), mentorshipDto.getMenteeId());
    }
}
