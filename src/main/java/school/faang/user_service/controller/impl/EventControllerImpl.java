package school.faang.user_service.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.EventController;
import school.faang.user_service.dto.event.request.EventCreationRequest;
import school.faang.user_service.dto.event.response.EventCreationResponse;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.EventService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Override
    public ResponseEntity<EventCreationResponse> create(EventCreationRequest request, Long ownerId) {
        log.info("Получен запрос на создание события: {}", request);
        Event event = eventService.create(
                eventMapper.toEventEntity(request),
                request.getRelatedSkills(),
                ownerId);
        return new ResponseEntity<>(eventMapper.toEventCreationResponse(event), HttpStatus.CREATED);
    }
}
