package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

@RestController
@RequestMapping("/mentorship/request")
@RequiredArgsConstructor
@Validated
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@RequestBody @Valid MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/get")
    public Page<MentorshipRequestDto> getRequests(
            @RequestBody RequestFilterDto filter,
            @PageableDefault(size = 10, page = 0, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return mentorshipRequestService.getRequests(filter, pageable);
    }

    @PatchMapping("/{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable @Min(1) long requestId){
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @PatchMapping("/{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@PathVariable @Min(1) long requestId, @RequestBody @Valid RejectionDto rejection){
        return mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}