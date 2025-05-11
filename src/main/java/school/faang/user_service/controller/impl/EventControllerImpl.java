package school.faang.user_service.controller.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.EventController;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventCreationRequest;
import school.faang.user_service.dto.event.response.EventResponse;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Override
    public ResponseEntity<EventResponse> create(EventCreationRequest request, Long ownerId) {
        log.info("Получен запрос на создание события: {}", request);
        Event event = eventService.create(
                eventMapper.toEventEntity(request),
                request.getRelatedSkills(),
                ownerId);
        return new ResponseEntity<>(eventMapper.toEventResponse(event), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EventResponse> getEvent(Long id) {
        log.info("Получен запрос на получение события: {}", id);
        return new ResponseEntity<>(eventMapper.toEventResponse(eventService.getEvent(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EventResponse>> getEventsByFilter(EventFilterDto filter) {
        log.info("Получен запрос на поиск по фильтру: {}", filter);
        return new ResponseEntity<>(eventMapper.toEventResponses(
                eventService.getEventsByFilter(filter)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteEvent(long id) {
        log.info("Получен запрос на удаление иваента с id: {}", id);
        return new ResponseEntity<>(eventService.deleteEvent(id), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EventResponse>> getOwnedEvents(long ownerId) {
        log.info("Получен запрос на получение иваентов пользователя с userId: {}", ownerId);
        return new ResponseEntity<>(eventMapper.toEventResponses(eventService.getOwnedEvents(ownerId)),
                HttpStatus.OK);
    }
}
