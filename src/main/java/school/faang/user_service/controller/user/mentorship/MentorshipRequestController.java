package school.faang.user_service.controller.user.mentorship;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    MentorshipRequestDto toMentorshipRequestDto(
            @Valid @RequestBody CreateMentorshipRequestDto createMentorshipRequestDto) {
        return mentorshipRequestService.create(createMentorshipRequestDto);
    }

    @PostMapping("/filtering")
    List<MentorshipRequestDto> getByFilters(@Valid @RequestBody MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getByFilters(filter);
    }

    @PutMapping("/accept/{requestId}")
    void accept(@PathVariable @Positive long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    @PutMapping("/reject/{requestId}")
    void reject(@PathVariable @Positive long requestId, @Valid @RequestBody RejectionDto rejectionDto) {
        mentorshipRequestService.reject(requestId, rejectionDto);
    }
}