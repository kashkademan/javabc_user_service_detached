package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RegisterParticipantRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventParticipationServiceTest {

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Spy
    private UserMapperImpl registerParticipantRequestMapper;

    @InjectMocks
    private EventParticipationService eventParticipationService;

    private static final long eventId = 13L;
    private static final long userId = 46L;

    @Test
    public void testRegisterParticipantSuccess() {
        eventParticipationService.registerParticipant(eventId, userId);
        verify(eventParticipationRepository).register(eventId, userId);
        verifyNoMoreInteractions(eventParticipationRepository);
    }

    @Test
    public void testRegisterParticipant_RepositoryThrowsException() {
        RuntimeException expectedException = new RuntimeException("Simulated repository error");
        doThrow(expectedException).when(eventParticipationRepository).register(eventId, userId);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            eventParticipationService.registerParticipant(eventId, userId);
        });

        assertEquals("Error registering user for event.", actualException.getMessage());
        assertEquals(expectedException, actualException.getCause());
        verify(eventParticipationRepository).register(eventId, userId);
        verifyNoMoreInteractions(eventParticipationRepository);
    }

    @Test
    public void testUnregisterParticipantSuccess() {

        eventParticipationService.unregisterParticipant(eventId, userId);

        verify(eventParticipationRepository).unregister(eventId, userId);
        verifyNoMoreInteractions(eventParticipationRepository);
    }

    @Test
    public void testUnregisterParticipant_RepositoryThrowsException() {
        RuntimeException expectedException = new RuntimeException("Simulated repository error");
        doThrow(expectedException).when(eventParticipationRepository).unregister(eventId, userId);

        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            eventParticipationService.unregisterParticipant(eventId, userId);
        });

        assertEquals("Error unregistering user from event.", actualException.getMessage());
        assertEquals(expectedException, actualException.getCause());
        verify(eventParticipationRepository).unregister(eventId, userId);
        verifyNoMoreInteractions(eventParticipationRepository);
    }

    @Test
    public void testGetParticipantSuccess() {
        User user1 = createUser(1L, "user1");
        User user2 = createUser(2L, "user2");

        List<User> users = List.of(user1, user2);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(users);

        List<RegisterParticipantRequestDto> actualDtoList = eventParticipationService.getParticipant(eventId);

        assertNotNull(actualDtoList);
        assertEquals(2, actualDtoList.size());

        assertEquals(user1.getId(), actualDtoList.get(0).getId());
        assertEquals(user2.getId(), actualDtoList.get(1).getId());

        verifyNoMoreInteractions(eventParticipationRepository);
    }

    @Test
    public void testGetParticipantsCountSuccess() {
        int expectedCount = 5;
        when(eventParticipationRepository.countParticipants(eventId))
                .thenReturn(expectedCount);

        int actualCount = eventParticipationService.getParticipantsCount(eventId);

        assertEquals(expectedCount, actualCount);
        verify(eventParticipationRepository).countParticipants(eventId);
        verifyNoMoreInteractions(eventParticipationRepository);
    }

    private User createUser(Long id, String username) {
        return User.builder().id(id).username(username).build();
    }
}