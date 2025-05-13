package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventRequest;
import school.faang.user_service.dto.event.response.EventResponse;

import java.util.List;

@RequestMapping("/api/v1/events")
@Validated
public interface EventController {
    @PostMapping
    ResponseEntity<EventResponse> create(@RequestBody @Valid EventRequest request);

    @PatchMapping("/{id}")
    ResponseEntity<EventResponse> updateEvent(@RequestBody @Valid EventRequest request,
                                              @NotNull @Positive @PathVariable long id);

    @GetMapping("/{id}")
    ResponseEntity<EventResponse> getEvent(@NotNull @Positive @PathVariable long id);

    @GetMapping("/filter")
    ResponseEntity<List<EventResponse>> getEventsByFilter(@RequestBody EventFilterDto filter);

    @GetMapping("/owner")
    ResponseEntity<List<EventResponse>> getOwnedEvents();

    @GetMapping("/participation")
    ResponseEntity<List<EventResponse>> getParticipatedEvents();

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEvent(@NotNull @Positive @PathVariable long id);
}
