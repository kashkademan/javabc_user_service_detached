package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.EventParticipationDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.event.EventParticipationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService service;

    @InjectMocks
    private EventParticipationController controller;

    private long eventId = 1L;
    private long userId = 2L;
    private UserDto userDto;
    private CountResponse countResponse;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "user", "user@test.com", "1234567890", "About me");
        countResponse = new CountResponse(5);
    }

    @Test
    void registerParticipant_shouldReturnDto() {
        EventParticipationDto eventParticipationDto = new EventParticipationDto(eventId, userId);
        when(service.registerParticipant(eventId, userId)).thenReturn(eventParticipationDto);

        EventParticipationDto result = controller.registerParticipant(eventId, userId);

        assertEquals(eventParticipationDto, result);
        verify(service).registerParticipant(eventId, userId);
    }

    @Test
    void unregisterParticipant_shouldCallService() {
        controller.unregisterParticipant(eventId, userId);
        verify(service).unregisterParticipant(eventId, userId);
    }

    @Test
    void countParticipantsByEventId_shouldReturnCount() {
        when(service.countParticipantsByEventId(eventId)).thenReturn(countResponse);

        CountResponse result = controller.countParticipantsByEventId(eventId);

        assertEquals(countResponse, result);
        verify(service).countParticipantsByEventId(eventId);
    }

    @Test
    void getAllParticipantsByEventId_shouldReturnList() {
        when(service.getAllParticipantsByEventId(eventId)).thenReturn(List.of(userDto));

        List<UserDto> result = controller.getAllParticipantsByEventId(eventId);

        assertEquals(1, result.size());
        assertSame(userDto, result.get(0));
        verify(service).getAllParticipantsByEventId(eventId);
    }
}
