package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/mentorship-requests")
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping
    public MentorshipRequestDto requestMentorship(
            @Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("Request mentorship by user with id {} - Started", mentorshipRequestDto.getRequesterId());
        MentorshipRequestDto requestResult = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        log.info("Request mentorship by user with id {} - Finished", mentorshipRequestDto.getRequesterId());
        return requestResult;
    }

    @GetMapping
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        log.info("Getting mentorship requests with filter {} - Started", filter);
        List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filter);
        log.info("Getting mentorship requests with filter {} - Finished", filter);
        return requests;
    }

    @PatchMapping("/{requestId}/accept")
    public MentorshipRequestDto acceptRequest(@NotNull @PathVariable("requestId") Long id) {
        log.info("Accepting mentorship request with id {} - Started", id);
        MentorshipRequestDto acceptedRequest = mentorshipRequestService.acceptRequest(id);
        log.info("Accepting mentorship request with id {} - Finished", id);
        return acceptedRequest;
    }

    @PatchMapping("/{requestId}/reject")
    public MentorshipRequestDto rejectRequest(@NotNull @PathVariable("requestId") Long id,
                                              @Valid @RequestBody RejectionDto rejectionDto) {
        log.info("Rejecting mentorship request with id {} - Started", id);
        MentorshipRequestDto rejectedRequest = mentorshipRequestService.rejectRequest(id, rejectionDto);
        log.info("Rejecting mentorship request with id {} - Finished", id);
        return rejectedRequest;
    }
}