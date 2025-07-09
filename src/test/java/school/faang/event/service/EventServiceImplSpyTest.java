package school.faang.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.EventServiceImpl;
import school.faang.user_service.service.filter.EventFilter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventServiceImplSpyTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private EventFilter filter1;
    @Mock
    private EventFilter filter2;

    private EventServiceImpl eventService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        eventService = spy(new EventServiceImpl(
                eventRepository,
                userRepository,
                eventMapper,
                userContext,
                List.of(filter1, filter2)
        ));
    }

    @Test
    @DisplayName("Update вызывает validateOwnerSkills и обновляет событие")
    void testUpdate_callsValidateOwnerSkills() {
        long userId = 1L;
        long eventId = 10L;

        User owner = new User();
        owner.setId(userId);
        owner.setSkills(List.of(new Skill() {{
                setId(1L);
            }}
        ));

        Event event = new Event();
        event.setId(eventId);
        event.setOwner(owner);

        EventUpdateDto updateDto = new EventUpdateDto();

        when(userContext.getUserId()).thenReturn(userId);
        when(eventRepository.getByIdOrThrow(eventId)).thenReturn(event);
        doNothing().when(eventService).validateOwnerSkills(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toViewDto(event)).thenReturn(new EventViewDto());

        final EventViewDto result = eventService.update(eventId, updateDto);

        verify(eventService).validateOwnerSkills(event);
        verify(eventMapper).update(updateDto, event);
        verify(eventRepository).save(event);

        assertNotNull(result);
    }

    @Test
    @DisplayName("validateOwnerSkills выбрасывает DataValidationException, если навык отсутствует у владельца")
    void testValidateOwnerSkills_throwsException() {
        User owner = new User();
        owner.setId(1L);
        owner.setSkills(List.of(new Skill() {{
                setId(1L);
            }}
        ));

        Event event = new Event();
        event.setOwner(owner);
        event.setRelatedSkills(List.of(new Skill() {{
                setId(2L);
            }}
        ));

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> eventService.validateOwnerSkills(event)
        );

        assertEquals("Owner does not have all required skills for this event", ex.getMessage());
    }
}
