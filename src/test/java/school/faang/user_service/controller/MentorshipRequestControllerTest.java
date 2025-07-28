package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestControllerTest {

    @Mock
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;

    @Test
    void testRejectThrowsExceptionWhenRejectionDtoIsNull() {
        long requestId = 1L;

        // Настройка: когда reject вызывается с null — выбрасывается исключение
        doThrow(new DataValidationException("Причина отказа должна быть указана"))
                .when(mentorshipRequestService)
                .reject(eq(requestId), isNull());

        // Проверка: контроллер должен пробросить исключение
        assertThrows(DataValidationException.class, () ->
                mentorshipRequestController.reject(requestId, null)
        );

        verify(mentorshipRequestService).reject(eq(requestId), isNull());
    }

    @Test
    void testCreate() {
        // Arrange
        CreateMentorshipRequestDto createDto = new CreateMentorshipRequestDto("Help with Spring Boot", 2L);

        UserDto requester = new UserDto(1L, "Requester", null, null, null);
        UserDto receiver = new UserDto(2L, "Receiver", null, null, null);

        MentorshipRequestDto expectedDto = new MentorshipRequestDto(
                123L,
                "Help with Spring Boot",
                requester,
                receiver,
                RequestStatus.PENDING
        );

        when(mentorshipRequestService.create(createDto)).thenReturn(expectedDto);

        // Act
        MentorshipRequestDto result = mentorshipRequestController.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.description(), result.description());
        assertEquals(expectedDto.requester().id(), result.requester().id());
        assertEquals(expectedDto.receiver().id(), result.receiver().id());
        assertEquals(expectedDto.status(), result.status());

        verify(mentorshipRequestService).create(createDto);
    }

    @Test
    void testCreateThrowsExceptionOnInvalidInput() {
        CreateMentorshipRequestDto invalidDto = new CreateMentorshipRequestDto(null, null);

        when(mentorshipRequestService.create(invalidDto))
                .thenThrow(new DataValidationException("Invalid input"));

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestController.create(invalidDto);
        });

        assertEquals("Invalid input", thrown.getMessage());
        verify(mentorshipRequestService).create(invalidDto);
    }

    @Test
    void testGetByFilters() {
        // Arrange
        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(2L);
        filterDto.setStatus(RequestStatus.PENDING);

        UserDto requester = new UserDto(1L, "Requester", null, null, null);
        UserDto receiver = new UserDto(2L, "Receiver", null, null, null);

        MentorshipRequestDto requestDto = new MentorshipRequestDto(
                123L,
                "Test description",
                requester,
                receiver,
                RequestStatus.PENDING
        );

        when(mentorshipRequestService.getByFilters(filterDto)).thenReturn(List.of(requestDto));

        // Act
        List<MentorshipRequestDto> result = mentorshipRequestController.getByFilters(filterDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(requestDto.description(), result.get(0).description());
        assertEquals(requestDto.requester().id(), result.get(0).requester().id());

        verify(mentorshipRequestService).getByFilters(filterDto);
    }

    @Test
    void testGetByFiltersReturnsEmptyList() {
        MentorshipRequestFilterDto emptyFilter = new MentorshipRequestFilterDto();

        when(mentorshipRequestService.getByFilters(emptyFilter)).thenReturn(List.of());

        List<MentorshipRequestDto> result = mentorshipRequestController.getByFilters(emptyFilter);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(mentorshipRequestService).getByFilters(emptyFilter);
    }

    @Test
    void testGetByFiltersThrowsException() {
        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();

        when(mentorshipRequestService.getByFilters(filterDto))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            mentorshipRequestController.getByFilters(filterDto);
        });

        assertEquals("Unexpected error", thrown.getMessage());
        verify(mentorshipRequestService).getByFilters(filterDto);
    }

    @Test
    void testAccept() {
        // Arrange
        long requestId = 1L;

        doNothing().when(mentorshipRequestService).accept(requestId);

        // Act
        mentorshipRequestController.accept(requestId);

        // Assert
        verify(mentorshipRequestService).accept(requestId);
    }

    @Test
    void testAcceptThrowsException() {
        long requestId = 1L;
        doThrow(new DataValidationException("Invalid request"))
                .when(mentorshipRequestService).accept(requestId);

        DataValidationException thrown = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestController.accept(requestId);
        });

        assertEquals("Invalid request", thrown.getMessage());
        verify(mentorshipRequestService).accept(requestId);
    }

    @Test
    void testReject() {
        // Arrange
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("Not a good time");

        // Здесь нет возврата, просто проверим вызов сервиса
        doNothing().when(mentorshipRequestService).reject(requestId, rejectionDto);

        // Act
        mentorshipRequestController.reject(requestId, rejectionDto);

        // Assert
        verify(mentorshipRequestService).reject(requestId, rejectionDto);
    }

    @Test
    void testRejectThrowsExceptionWhenReasonIsBlank() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("");

        doThrow(new DataValidationException("Причина отказа должна быть указана"))
                .when(mentorshipRequestService)
                .reject(eq(requestId), any(RejectionDto.class));

        assertThrows(DataValidationException.class,
                () -> mentorshipRequestController.reject(requestId, rejectionDto));

        verify(mentorshipRequestService).reject(requestId, rejectionDto);
    }

}