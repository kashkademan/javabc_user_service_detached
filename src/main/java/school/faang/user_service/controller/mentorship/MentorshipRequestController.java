package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

/**
 * REST-контроллер для работы с запросами на менторство.
 * <p>
 * Позволяет создавать запросы, получать список по фильтрам,
 * а также принимать и отклонять запросы на менторство.
 * </p>
 */
@RestController
@RequestMapping("/mentorship-requests")
@RequiredArgsConstructor
@Tag(name = "Менторские запросы", description = "Управление запросами на наставничество")
public class MentorshipRequestController {

    private final MentorshipRequestService service;

    @PostMapping
    @Operation(summary = "Создать запрос на наставничество", description = "Создаёт новый запрос и возвращает его представление")
    public ResponseEntity<MentorshipRequestViewDto> addMentorshipRequest(
            @Valid @RequestBody MentorshipRequestCreateDto dto) {
        MentorshipRequestViewDto result = service.create(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Получить список запросов", description = "Возвращает список запросов на наставничество по фильтрам")
    public ResponseEntity<List<MentorshipRequestViewDto>> getByFilters(
            MentorshipRequestFilterDto filter) {
        List<MentorshipRequestViewDto> requests = service.getByFilters(filter);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{requestId}/accept")
    @Operation(summary = "Принять запрос", description = "Принимает запрос на наставничество по ID")
    public ResponseEntity<Void> accept(@PathVariable long requestId) {
        service.accept(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{requestId}/reject")
    @Operation(summary = "Отклонить запрос", description = "Отклоняет запрос на наставничество с указанием причины")
    public ResponseEntity<Void> reject(
            @PathVariable long requestId,
            @Valid @RequestBody RejectionDto rejectionDto) {
        service.reject(requestId, rejectionDto);
        return ResponseEntity.ok().build();
    }
}