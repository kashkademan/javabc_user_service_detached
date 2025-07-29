package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @Test
    void create_throwsExceptionIfRequestIsTooFrequent() {
        final long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        final MentorshipRequest lastRequest = createRequest(userId, userId + 1, RequestStatus.PENDING);
        lastRequest.setCreatedAt(LocalDateTime.now().minusDays(10));
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId)).
                thenReturn(Optional.of(lastRequest));

        final CreateMentorshipRequestDto dto = createDto("desc", userId + 1);

        final DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.create(dto);
        });

        assertTrue(ex.getMessage().contains("не чаще одного раза"));
    }

    @Test
    void create_throwsExceptionIfRequestToSelf() {
        final long userId = 1L;
        final CreateMentorshipRequestDto dto = createDto("Хочу быть сам себе ментором", userId);

        when(userContext.getUserId()).thenReturn(userId);

        final DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.create(dto);
        });

        assertEquals("Нельзя отправить запрос самому себе", exception.getMessage());
    }

    @Test
    void create_throwsExceptionIfActiveRequestExists() {
        final long userId = 1L;
        final long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId)).
                thenReturn(Optional.empty());
        when(mentorshipRequestRepository.findLatestRequest(userId, mentorId)).thenReturn(Optional.
                of(createRequest(userId, mentorId, RequestStatus.PENDING)));

        final CreateMentorshipRequestDto dto = createDto("desc", mentorId);

        final DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.create(dto);
        });

        assertTrue(ex.getMessage().contains("Уже существует активный запрос"));
    }

    @Test
    void create_successfulCreation() {
        final long userId = 1L;
        final long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId)).
                thenReturn(Optional.empty());
        when(mentorshipRequestRepository.findLatestRequest(userId, mentorId)).thenReturn(Optional.empty());

        final CreateMentorshipRequestDto dto = createDto("desc", mentorId);
        final MentorshipRequest newRequest = createRequest(userId, mentorId, RequestStatus.PENDING);
        newRequest.setCreatedAt(LocalDateTime.now());

        when(mentorshipRequestRepository.create(userId, mentorId, dto.description())).thenReturn(newRequest);
        when(userRepository.findById(userId)).thenReturn(Optional.of(createUser(userId)));
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(createUser(mentorId)));

        final MentorshipRequestDto responseDto = createResponseDto();
        when(mentorshipRequestMapper.toMentorshipRequestDto(newRequest)).thenReturn(responseDto);

        final MentorshipRequestDto result = mentorshipRequestService.create(dto);

        assertEquals(responseDto, result);
        verify(mentorshipRequestRepository).create(userId, mentorId, dto.description());
        verify(userRepository, times(2)).findById(anyLong());
        verify(mentorshipRequestMapper).toMentorshipRequestDto(newRequest);
    }

    @Test
    void getByFilters_filterByReceiverId() {
        final MentorshipRequest req1 = createRequest(1L, 2L, RequestStatus.PENDING);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(req1));

        final MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setReceiverId(2L);

        final MentorshipRequestDto dto = createResponseDto();
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        final List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_filterByStatus() {
        final MentorshipRequest req1 = createRequest(1L, 2L, RequestStatus.ACCEPTED);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(req1));

        final MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);

        final MentorshipRequestDto dto = createResponseDto();
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        final List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_filterByMultipleFields() {
        final MentorshipRequest req1 = createRequest(1L, 2L, RequestStatus.PENDING);
        when(mentorshipRequestRepository.findAll()).thenReturn(List.of(req1));

        final MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(2L);
        filterDto.setStatus(RequestStatus.PENDING);

        final MentorshipRequestDto dto = createResponseDto();
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        final List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_throwsExceptionIfNoRequesterOrReceiver() {
        final MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();

        final DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.getByFilters(filterDto);
        });

        assertTrue(ex.getMessage().contains("Хотя бы один из параметров"));
    }

    private CreateMentorshipRequestDto createDto(String description, long receiverId) {
        return new CreateMentorshipRequestDto(description, receiverId);
    }

    private User createUser(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private MentorshipRequest createRequest(long requesterId, long receiverId, RequestStatus status) {
        MentorshipRequest request = new MentorshipRequest();
        request.setRequester(createUser(requesterId));
        request.setReceiver(createUser(receiverId));
        request.setStatus(status);
        return request;
    }

    private MentorshipRequestDto createResponseDto() {
        return new MentorshipRequestDto(null, null, null, null, null);
    }
}
