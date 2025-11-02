package school.faang.user_service.service.events;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl service;

    @Captor
    ArgumentCaptor<Event> captorEvent;

    @Test
    public void createEvent_authorNotSkill_shouldThrowForbiddenException() {
        EventCreateDto dto = EventCreateDto.builder()
                .relatedSkillsId(new ArrayList<>(List.of(5L)))
                .build();
        List<SkillDto> listSkillDto = preparingSkillDtoList();
        when(userContext.getUserId()).thenReturn(0L);
        when(skillService.getByUserId(0L)).thenReturn(listSkillDto);

        Assert.assertThrows(ForbiddenException.class,
                () -> service.createEvent(dto));
    }

    @Test
    public void createEvent_authorAlreadyHaveEvent_shouldThrowForbiddenException() {

        Event event = new Event();
        event.setTitle("Test");
        event.setOwner(User.builder().id(4L).build());
        User user = User.builder()
                .id(4L)
                .ownedEvents(new ArrayList<>(List.of(event)))
                .build();

        when(userContext.getUserId()).thenReturn(4L);
        when(userRepository.getByIdOrThrow(4L)).thenReturn(user);
        EventCreateDto dto = EventCreateDto.builder()
                .relatedSkillsId(new ArrayList<>(List.of(1L)))
                .title("Test")
                .build();
        Assert.assertThrows(ForbiddenException.class,
                () -> service.createEvent(dto));
    }

    @Test
    public void createEvent_responseDto_shouldReturnEventResponseDto() {
        Event event = new Event();
        event.setTitle("TestTest");
        event.setOwner(User.builder().id(4L).build());

        Event savedEvent = new Event();
        savedEvent.setTitle("TestTest");
        savedEvent.setOwner(User.builder().id(4L).build());

        EventResponseDto response = EventResponseDto.builder()
                .title("TestTest")
                .build();
        when(userContext.getUserId()).thenReturn(4L);
        User user = User.builder()
                .id(4L)
                .ownedEvents(new ArrayList<>(List.of(event)))
                .build();
        when(userRepository.getByIdOrThrow(4L)).thenReturn(user);
        when(skillRepository.findSkillByIds(anyList())).thenReturn(new ArrayList<>());
        EventCreateDto dto = EventCreateDto.builder()
                .relatedSkillsId(new ArrayList<>(List.of(1L)))
                .title("Test")
                .build();
        when(eventMapper.toEntityCreate(dto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(captorEvent.capture());
        when(eventMapper.toDto(savedEvent)).thenReturn(response);

        EventResponseDto responseDto = service.createEvent(dto);

        verify(eventRepository).save(event);
        assertEquals("TestTest", responseDto.title());
    }

    @Test
    public void updateEvent_notValidAuthorEvent_shouldThrowForbiddenException() {
        UpdateEventDto dto = UpdateEventDto.builder()
                .build();
        User user = User.builder()
                .id(1L)
                .build();
        Event event = new Event();
        event.setOwner(user);

        when(eventRepository.getByIdOrThrow(1L)).thenReturn(event);

        assertThrows(ForbiddenException.class,
                () -> service.updateEvent(1L, dto));
    }

    @Test
    public void updateEvent_authorNotHaveSkill_shouldThrowForbiddenException() {
        User user = User.builder()
                .id(1L)
                .build();
        Event event = new Event();
        event.setOwner(user);

        List<SkillDto> skillDtoList = preparingSkillDtoList();
        when(eventRepository.getByIdOrThrow(1L)).thenReturn(event);
        when(userContext.getUserId()).thenReturn(1L);
        when(skillService.getByUserId(1L)).thenReturn(skillDtoList);
        UpdateEventDto dto = UpdateEventDto.builder()
                .relatedSkillsId(new ArrayList<>(List.of(6L, 7L, 9L)))
                .build();
        assertThrows(ForbiddenException.class,
                () -> service.updateEvent(1L, dto));
    }

    @Test
    public void updateEvent_updateEvent_shouldUpdateEvent() {
        User user = User.builder()
                .id(1L)
                .build();
        Event event = new Event();
        event.setOwner(user);
        Event updateEvent = new Event();
        List<SkillDto> skillDtoList = preparingSkillDtoList();
        EventResponseDto response = EventResponseDto.builder().build();

        when(eventRepository.getByIdOrThrow(1L)).thenReturn(event);
        when(userContext.getUserId()).thenReturn(1L);
        when(skillService.getByUserId(1L)).thenReturn(skillDtoList);
        UpdateEventDto dto = UpdateEventDto.builder()
                .relatedSkillsId(new ArrayList<>(List.of(1L)))
                .build();
        when(eventMapper.update(dto, event)).thenReturn(updateEvent);
        when(skillRepository.findSkillByIds(dto.relatedSkillsId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(captorEvent.capture())).thenReturn(updateEvent);
        when(eventMapper.toDto(updateEvent)).thenReturn(response);

        service.updateEvent(1L, dto);

        verify(eventRepository).save(updateEvent);

        Event capturedEvent = captorEvent.getValue();
        Assertions.assertNotNull(capturedEvent);
    }

    @Test
    public void getAllByFilter_responseList_shouldListEventResponseDto() {
        Event event = new Event();
        List<Event> events = new ArrayList<>(List.of(event));
        EventResponseDto response = EventResponseDto.builder()
                .title("Test")
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "startDate"));
        AllEventByFilterDto dto = AllEventByFilterDto.builder()
                .titleContains("Test")
                .descriptionContains("TestDescription")
                .type(EventType.WEBINAR)
                .ownerId(1L)
                .participantId(2L)
                .build();
        when(eventRepository.findEventsByFilters(dto.titleContains(), dto.descriptionContains(), dto.type(),
                dto.ownerId(),
                dto.participantId(), pageable)).thenReturn(events);
        when(eventMapper.toDto(event)).thenReturn(response);

        List<EventResponseDto> eventResponseDto = service.getAllByFilter(dto, 0, 10);

        Assertions.assertEquals(response.title(), eventResponseDto.get(0).title());
    }

    @Test
    public void deleteEvent_throwForbidden_shouldThrowForbiddenException() {
        Event event = new Event();
        event.setOwner(User.builder().id(1L).build());
        when(eventRepository.getByIdOrThrow(1L)).thenReturn(event);

        assertThrows(ForbiddenException.class,
                () -> service.deleteEvent(1L));
    }

    @Test
    public void deleteEvent_deleteEvent_shouldDeleteEvent() {
        Event event = new Event();
        event.setOwner(User.builder().id(1L).build());
        when(eventRepository.getByIdOrThrow(1L)).thenReturn(event);
        when(userContext.getUserId()).thenReturn(1L);

        service.deleteEvent(1L);

        verify(eventRepository).delete(event);
    }


    private List<SkillDto> preparingSkillDtoList() {
        SkillDto skillDto =
                new SkillDto(1L, "Tests", LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        return new ArrayList<>(List.of(skillDto));
    }
}