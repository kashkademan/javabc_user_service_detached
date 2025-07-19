package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

/**
 * REST контроллер для управления запросами на рекомендации
 * <p>
 * Предоставляет конечные точки для создания, фильтрации, получения, принятии и отклонении
 * запросов на рекомендацию
 * </p>
 *
 * <ul>
 *     <li>POST /recommendations - создание нового запроса на рекомендацию</li>
 *     <li>POST  /recommendations/{requestId}/accept - принятие существующего запроса</li>
 *     <li>POST /recommendations/{requestId}/rejected - отклонение существующего запроса</li>
 *     <li>GET /recommendations - получение списка отфильтрованных запросов</li>
 *     <li>GET /recommendations/{requestId} - получение конкретного запроса</li>
 * </ul>
 * <p>
 * Использует {@link RecommendationRequestService} для бизнес-логики
 *
 * @author Linempy
 * @since 14.07.2025
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recommendations")
public class RecommendationRequestController {
    private final RecommendationRequestService service;

    @PostMapping
    public ResponseEntity<RecommendationRequestViewDto> create(
            @Valid @RequestBody RecommendationRequestCreateDto recommendationDto) {
        RecommendationRequestViewDto created = service.create(recommendationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<RecommendationRequestViewDto> getByFilters(
            @ModelAttribute @Valid RecommendationRequestFilterDto filter) {
        return service.getByFilters(filter);
    }

    @GetMapping("/{requestId}")
    public RecommendationRequestViewDto getById(@PathVariable long requestId) {
        return service.getById(requestId);
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> accept(@PathVariable long requestId) {
        service.accept(requestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> reject(@PathVariable long requestId, @RequestBody @Valid RejectionDto rejectionDto) {
        service.reject(requestId, rejectionDto);
        return ResponseEntity.ok().build();
    }
}

