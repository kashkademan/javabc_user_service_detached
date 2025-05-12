package school.faang.user_service.controller.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventRequest;
import school.faang.user_service.dto.event.response.EventResponse;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EventControllerImpl implements EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Override
    public ResponseEntity<EventResponse> create(EventRequest request) {
        log.info("Получен запрос на создание события: {}", request);
        Event event = eventService.create(
                eventMapper.eventRequestToEventEntity(request),
                request.getRelatedSkills(),
                request.getOwnerId());
        return new ResponseEntity<>(eventMapper.eventToEventResponse(event), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<EventResponse> updateEvent(EventRequest request, long id) {
        log.info("Получен запрос на обновление иваента: {}", request);
        Event updatedEvent = eventService.updateEvent(
                eventMapper.eventRequestToEventEntity(request),
                request.getRelatedSkills(),
                request.getOwnerId(), id);
        return new ResponseEntity<>(eventMapper.eventToEventResponse(updatedEvent), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EventResponse> getEvent(long id) {
        log.info("Получен запрос на получение события: {}", id);
        return new ResponseEntity<>(eventMapper.eventToEventResponse(eventService.getEvent(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EventResponse>> getEventsByFilter(EventFilterDto filter) {
        log.info("Получен запрос на поиск по фильтру: {}", filter);
        return new ResponseEntity<>(eventMapper.toEventResponses(
                eventService.getEventsByFilter(filter)),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EventResponse>> getOwnedEvents() {
        return new ResponseEntity<>(eventMapper.toEventResponses(eventService.getOwnedEvents()),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<EventResponse>> getParticipatedEvents() {
        return new ResponseEntity<>(eventMapper.toEventResponses(eventService.getParticipatedEvents()),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> deleteEvent(long id) {
        log.info("Получен запрос на удаление иваента с id: {}", id);
        return new ResponseEntity<>(eventService.deleteEvent(id), HttpStatus.OK);
    }
}
