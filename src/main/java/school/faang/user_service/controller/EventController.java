package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.event.EventDto;

@RequestMapping("/api/v1/events")
public interface EventController {
    @PostMapping
    ResponseEntity<EventDto> create(@RequestBody @Valid EventDto event);
}
