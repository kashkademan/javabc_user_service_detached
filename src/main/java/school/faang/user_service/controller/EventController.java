package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.request.EventCreationRequest;
import school.faang.user_service.dto.event.response.EventCreationResponse;

@RequestMapping("/api/v1/events")
@Validated
public interface EventController {
    @PostMapping("/{ownerId}")
    ResponseEntity<EventCreationResponse> create(@RequestBody @Valid EventCreationRequest request,
                                                 @Positive @PathVariable Long ownerId);
}
