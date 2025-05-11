package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventCreationRequest;
import school.faang.user_service.dto.event.response.EventCreationResponse;

import java.util.List;

@RequestMapping("/api/v1/events")
@Validated
public interface EventController {
    @PostMapping("/{ownerId}")
    ResponseEntity<EventCreationResponse> create(@RequestBody @Valid EventCreationRequest request,
                                                 @NotNull @Positive @PathVariable Long ownerId);

    @GetMapping("/{id}")
    ResponseEntity<EventCreationResponse> getEvent(@NotNull @Positive @PathVariable Long id);

    @GetMapping("/filter")
    ResponseEntity<List<EventCreationResponse>> getEventsByFilter(@RequestBody EventFilterDto filter);

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEvent(@NotNull @Positive @PathVariable long id);
}
