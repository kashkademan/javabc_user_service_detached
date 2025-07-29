package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.TestEventDescriptionFilter;
import school.faang.user_service.filter.TestEventOwnerFilter;
import school.faang.user_service.filter.TestEventParticipantFilter;
import school.faang.user_service.filter.TestEventTitleFilter;
import school.faang.user_service.filter.TestEventTypeFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private EventServiceImpl eventServiceImpl;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Captor
    ArgumentCaptor<Event> captor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    private final EventFilter eventFilterTitle = new TestEventTitleFilter();
    private final EventFilter eventFilterDescription = new TestEventDescriptionFilter();
    private final EventFilter eventFilterOwner = new TestEventOwnerFilter();
    private final EventFilter eventFilterParticipant = new TestEventParticipantFilter();
    private final EventFilter eventFilterType = new TestEventTypeFilter();

    private CreateEventDto eventDto;

    private UpdateEventDto updateEventDto;

    private Event event;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(10L).username("Alex").build();

        eventDto = CreateEventDto.builder()
                .title("Event")
                .description("Test Event")
                .ownerId(owner.getId())
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .type(EventType.PRESENTATION)
                .build();

        event = Event.builder().id(100L).title(eventDto
                .title()).description(eventDto.description()).owner(owner).build();

        updateEventDto = UpdateEventDto.builder()
                .title("New title").description("New description").startDate(LocalDateTime.now()
                        .plusDays(5)).endDate(LocalDateTime.now().plusDays(6)).type(EventType.PRESENTATION).build();

        eventServiceImpl = new EventServiceImpl(
                eventRepository, userRepository, eventMapper, userContext,
                List.of(eventFilterDescription,
                        eventFilterTitle,
                        eventFilterOwner,
                        eventFilterParticipant,
                        eventFilterType));
    }

    @DisplayName("create(): saves event when owner exists")
    @Test
    void testCreateEvent_Successful() {

        when(userRepository.getByIdOrThrow(eventDto.ownerId())).thenReturn(owner);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        eventServiceImpl.create(eventDto);

        verify(eventRepository, times(1)).save(captor.capture());
        Event capturedEvent = captor.getValue();

        assertEquals(eventDto.description(), capturedEvent.getDescription());
        assertEquals(eventDto.title(), capturedEvent.getTitle());
        assertEquals(eventDto.startDate(), capturedEvent.getStartDate());
        assertEquals(eventDto.endDate(), capturedEvent.getEndDate());
        assertEquals(eventDto.ownerId(), capturedEvent.getOwner().getId());

    }

    @DisplayName("create(): throws EntityNotFoundException when owner not found")
    @Test
    void testCreateEvent_OwnerNotFoundById() {
        when(userRepository.getByIdOrThrow(eventDto.ownerId())).thenThrow(new EntityNotFoundException(
                "Owner not found"));

        assertThrows(EntityNotFoundException.class, () -> eventServiceImpl.create(eventDto));
    }

    @DisplayName("update(): throws EntityNotFoundException when event not found")
    @Test
    void testUpdate_EventNotFoundById() {
        when(eventRepository.getByIdOrThrow(1L)).thenThrow(new EntityNotFoundException("Event not found"));

        assertThrows(EntityNotFoundException.class, () -> eventServiceImpl.update(1L, updateEventDto));
    }

    @DisplayName("update(): throws ForbiddenException when current user is not owner")
    @Test
    void testUpdate_ThrowsForbiddenIfUserNotOwner() {
        when(eventRepository.getByIdOrThrow(100L)).thenReturn(event);
        when(userContext.getUserId()).thenReturn(1L);
        assertThrows(ForbiddenException.class, () -> eventServiceImpl.update(100L, updateEventDto));
    }

    @DisplayName("update(): updates event successfully")
    @Test
    void testUpdateEvent_Successful() {

        when(eventRepository.getByIdOrThrow(event.getId())).thenReturn(event);
        when(userContext.getUserId()).thenReturn(owner.getId());
        when(eventRepository.save(event)).thenReturn(event);

        eventServiceImpl.update(100L, updateEventDto);

        verify(eventRepository, times(1)).save(captor.capture());
        Event capturedEvent = captor.getValue();

        assertEquals(updateEventDto.startDate(), capturedEvent.getStartDate());
        assertEquals(updateEventDto.endDate(), capturedEvent.getEndDate());
        assertEquals(updateEventDto.description(), capturedEvent.getDescription());
        assertEquals(updateEventDto.title(), capturedEvent.getTitle());
        assertEquals(updateEventDto.type(), capturedEvent.getType());
    }

    @DisplayName("getByFilters(): returns one EventDto when description, owner, participant, and type match")
    @Test
    void testGetByFilters_WhenMatchExistsReturnsOne() {
        Event event1 = Event.builder()
                .title("news")
                .description("new description")
                .owner(User.builder().id(1L)
                        .build())
                .attendees(List.of(User.builder().id(4L).build())).type(EventType.MEETING).build();
        Event event2 = Event.builder()
                .title("news")
                .description("new desc")
                .owner(User.builder().id(7L).build())
                .attendees(List.of(User.builder().id(5L).build()))
                .type(EventType.WEBINAR).build();

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventDto> result = eventServiceImpl.getByFilters(
                new EventFilterDto(null, null, null, null, null));

        assertEquals(1, result.size());
        assertTrue(result.get(0).description().contains("description"));
        assertEquals(1L, result.get(0).ownerId());
        assertTrue(result.get(0).participantIds().contains(4L));
        assertEquals(EventType.MEETING, result.get(0).type());
    }

    @DisplayName("getByFilters(): returns empty list when no event matches filter")
    @Test
    void testGetByFilters_WhenNoMatchReturnsEmpty() {
        Event event1 = Event.builder()
                .title("Title")
                .description("Desc")
                .owner(User.builder().id(10L).build())
                .attendees(List.of(User.builder().id(4L).build())).type(EventType.PRESENTATION).build();
        Event event2 = Event.builder()
                .title("news")
                .description("new desc")
                .owner(User.builder().id(7L).build())
                .attendees(List.of(User.builder().id(5L).build()))
                .type(EventType.WEBINAR).build();

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventDto> result = eventServiceImpl.getByFilters(
                new EventFilterDto(null, null, null, null, null));

        assertTrue(result.isEmpty());
    }

    @DisplayName("delete(): removes event when current user is owner")
    @Test
    void testDelete_Successful() {
        long eventId = 100L;

        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);
        when(userContext.getUserId()).thenReturn(owner.getId());

        eventServiceImpl.delete(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @DisplayName("delete(): throws EntityNotFoundException when event not found")
    @Test
    void testDelete_EventNotFound() {
        long eventId = 999L;
        when(eventRepository.getByIdOrThrow(eventId)).thenThrow(new EntityNotFoundException("Event not found"));

        assertThrows(EntityNotFoundException.class, () -> eventServiceImpl.delete(eventId));

        verify(eventRepository, never()).deleteById(anyLong());
    }

    @DisplayName("delete(): throws ForbiddenException when current user is not owner")
    @Test
    void testDelete_ThrowsForbiddenIfUserNotOwner() {
        when(eventRepository.getByIdOrThrow(event.getId())).thenReturn(event);
        when(userContext.getUserId()).thenReturn(42L);

        assertThrows(ForbiddenException.class, () -> eventServiceImpl.delete(event.getId()));

        verify(eventRepository, never()).deleteById(anyLong());
    }

}
