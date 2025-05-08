package school.faang.user_service.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.EventService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final EventService eventService;

    @Override
    public ResponseEntity<EventDto> create(EventDto event) {
        log.info("Получен запрос на создание события: {}", event);
        EventDto createdEvent = eventService.create(event);
        log.info("Событие успешно создано: {}", createdEvent);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }
}
