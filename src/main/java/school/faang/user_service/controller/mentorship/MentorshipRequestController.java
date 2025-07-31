package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipRequestController {

    @Autowired
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/add")
    public ResponseEntity<MentorshipRequestDto> create(@RequestBody @Valid CreateMentorshipRequestDto requestDto) {
        if (requestDto == null) {
            throw new DataValidationException("Запрос не найден");
        }
        MentorshipRequestDto response = mentorshipRequestService.create(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<MentorshipRequestDto>> getByFilters(
            @RequestBody @Valid MentorshipRequestFilterDto filter) {
        if (filter.requesterId() == null || filter.receiverId() == null) {
            throw new DataValidationException("requesterId и receiverId не могут быть пустыми");
        }
        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filter);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable("id") long requestId) {
        mentorshipRequestService.accept(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable("id") long requestId,
                                       @RequestBody @Valid RejectionDto rejectionDto) {
        if (rejectionDto.reason() == null || rejectionDto.reason().isBlank()) {
            throw new DataValidationException("Причина отказа не может быть пустой");
        }
        mentorshipRequestService.reject(requestId, rejectionDto);
        return ResponseEntity.ok().build();
    }
}
