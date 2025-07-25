package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestCreateDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestViewDto;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.util.List;

/**
 * REST-контроллер для работы с запросами на менторство.
 * <p>
 * Позволяет создавать запросы, получать список по фильтрам,
 * а также принимать и отклонять запросы на менторство.
 * </p>
 */
@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private final MentorshipRequestServiceImpl service;

    @PostMapping
    public ResponseEntity<MentorshipRequestViewDto> addMentorshipRequest(@RequestBody
                                                                         @Valid MentorshipRequestCreateDto dto) {
        MentorshipRequestViewDto result = service.create(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<MentorshipRequestViewDto>> getByFilters(MentorshipRequestFilterDto filter) {
        List<MentorshipRequestViewDto> requests = service.getByFilters(filter);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> accept(@PathVariable long requestId) {
        service.accept(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable long requestId,
            @Valid @RequestBody RejectionDto rejectionDto) {

        service.reject(requestId, rejectionDto);
        return ResponseEntity.ok().build();
    }
}