package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mentorship-requests")
@Validated
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto toMentorshipRequestDto(@Valid @RequestBody CreateMentorshipRequestDto createMentorshipRequestDto) {
        return mentorshipRequestService.create(createMentorshipRequestDto);
    }

    @GetMapping
    public List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getByFilters(filter);

    }

    @PutMapping("/{requestId}")
    public void accept(@PathVariable @NotNull Long requestId) {
        mentorshipRequestService.accept(requestId);
    }

    @PutMapping("/{requestId}/reject")
    public void reject(@PathVariable @NotNull Long requestId, @Valid RejectionDto rejectionDto) {
        mentorshipRequestService.reject(requestId, rejectionDto);
    }
}