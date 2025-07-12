package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto dto) {
        return mentorshipRequestService.requestMentorship(dto);
    }

    @PostMapping("/request/all")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/accept/{id}")
    public void acceptRequest(@PathVariable long id) {
        mentorshipRequestService.acceptRequest(id);
    }

    @PostMapping("/reject/{id}")
    public void rejectRequest(@PathVariable long id, @Valid @RequestBody RejectionDto rejection) {
        mentorshipRequestService.rejectRequest(id, rejection);
    }
}
