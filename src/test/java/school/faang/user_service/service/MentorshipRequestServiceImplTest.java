package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
import static org.mockito.Mockito.*;

public class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private MentorshipRequestServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentorshipRequestServiceImpl mentorshipRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- create method tests ---

    @Test
    void create_throwsExceptionIfRequestIsTooFrequent() {
        // Arrange
        long userId = 1L;
        when(userContext.getUserId()).thenReturn(userId);

        MentorshipRequest lastRequest = new MentorshipRequest();
        lastRequest.setCreatedAt(LocalDateTime.now().minusDays(10)); // меньше 3 месяцев
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(lastRequest));

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", userId + 1);

        // Act & Assert
        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.create(dto);
        });
        assertTrue(ex.getMessage().contains("не чаще одного раза"));
    }

    @Test
    void create_throwsExceptionIfRequestToSelf() {
        long userId = 1L;

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto(
                "Хочу быть сам себе ментором", userId
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User())); // <-- добавь это

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> service.create(dto));

        assertEquals("Нельзя отправить запрос самому себе", exception.getMessage());
    }

    @Test
    void create_throwsExceptionIfActiveRequestExists() {
        long userId = 1L;
        long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());

        MentorshipRequest activeRequest = new MentorshipRequest();
        activeRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findLatestRequest(userId, mentorId))
                .thenReturn(Optional.of(activeRequest));

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", userId + 1);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.create(dto);
        });
        assertTrue(ex.getMessage().contains("Уже существует активный запрос"));
    }

    @Test
    void create_successfulCreation() {
        long userId = 1L;
        long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());
        when(mentorshipRequestRepository.findLatestRequest(userId, mentorId))
                .thenReturn(Optional.empty());

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", userId + 1);

        MentorshipRequest newRequest = new MentorshipRequest();
        newRequest.setCreatedAt(LocalDateTime.now());
        newRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.create(userId, mentorId, dto.description()))
                .thenReturn(newRequest);

        User requester = new User();
        requester.setId(userId);
        User receiver = new User();
        receiver.setId(mentorId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(receiver));

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(null, null, null,
                null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(newRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.create(dto);

        assertEquals(mentorshipRequestDto, result);
        verify(mentorshipRequestRepository).create(userId, mentorId, dto.description());
        verify(userRepository, times(2)).findById(anyLong());
        verify(mentorshipRequestMapper).toMentorshipRequestDto(newRequest);
    }

    @Test
    void create_throwsExceptionIfUserNotFound() {
        long userId = 1L;
        long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());  // нет пользователя

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", mentorId);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            mentorshipRequestService.create(dto);
        });

        assertTrue(ex.getMessage().contains("менти с таким Id не найден"));
    }

    @Test
    void create_allowsRequestIfLastRequestOlderThanThreeMonths() {
        long userId = 1L;
        long mentorId = 2L;

        when(userContext.getUserId()).thenReturn(userId);

        MentorshipRequest lastRequest = new MentorshipRequest();
        lastRequest.setCreatedAt(LocalDateTime.now().minusMonths(4)); // больше 3 месяцев назад
        when(mentorshipRequestRepository.findTopByRequesterIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(lastRequest));

        when(mentorshipRequestRepository.findLatestRequest(userId, mentorId))
                .thenReturn(Optional.empty());

        CreateMentorshipRequestDto dto = new CreateMentorshipRequestDto("desc", mentorId);

        MentorshipRequest newRequest = new MentorshipRequest();
        newRequest.setCreatedAt(LocalDateTime.now());
        newRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.create(userId, mentorId, dto.description()))
                .thenReturn(newRequest);

        User requester = new User();
        requester.setId(userId);
        User receiver = new User();
        receiver.setId(mentorId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(mentorId)).thenReturn(Optional.of(receiver));

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto(null, null, null,
                null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(newRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.create(dto);

        assertEquals(mentorshipRequestDto, result);
    }

    // --- getByFilters tests ---

    @Test
    void getByFilters_filterByReceiverId() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest req1 = new MentorshipRequest();
        req1.setRequester(requester);
        req1.setReceiver(receiver);
        req1.setStatus(RequestStatus.PENDING);

        List<MentorshipRequest> allRequests = List.of(req1);

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);

        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setReceiverId(2L);

        MentorshipRequestDto dto = new MentorshipRequestDto(null, null, null, null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_filterByStatus() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest req1 = new MentorshipRequest();
        req1.setRequester(requester);
        req1.setReceiver(receiver);
        req1.setStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> allRequests = List.of(req1);

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);

        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);

        MentorshipRequestDto dto = new MentorshipRequestDto(null, null, null, null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_filterByMultipleFields() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest req1 = new MentorshipRequest();
        req1.setRequester(requester);
        req1.setReceiver(receiver);
        req1.setStatus(RequestStatus.PENDING);

        List<MentorshipRequest> allRequests = List.of(req1);

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);

        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);
        filterDto.setReceiverId(2L);
        filterDto.setStatus(RequestStatus.PENDING);

        MentorshipRequestDto dto = new MentorshipRequestDto(null, null, null, null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void getByFilters_throwsExceptionIfNoRequesterOrReceiver() {
        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.getByFilters(filterDto);
        });

        assertTrue(ex.getMessage().contains("Хотя бы один из параметров"));
    }

    @Test
    void getByFilters_returnsFilteredList() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest req1 = new MentorshipRequest();
        req1.setRequester(requester);
        req1.setReceiver(receiver);
        req1.setStatus(RequestStatus.PENDING);

        List<MentorshipRequest> allRequests = List.of(req1);

        when(mentorshipRequestRepository.findAll()).thenReturn(allRequests);

        MentorshipRequestFilterDto filterDto = new MentorshipRequestFilterDto();
        filterDto.setRequesterId(1L);

        MentorshipRequestDto dto = new MentorshipRequestDto(null, null, null,
                null, null);
        when(mentorshipRequestMapper.toMentorshipRequestDto(req1)).thenReturn(dto);

        List<MentorshipRequestDto> result = mentorshipRequestService.getByFilters(filterDto);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    // --- accept tests ---

    @Test
    void accept_successful() {
        long userId = 2L;
        long requestId = 10L;

        when(userContext.getUserId()).thenReturn(userId);

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(userId);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.existsByRequesterIdAndReceiverIdAndStatus(requester.getId(), userId,
                RequestStatus.ACCEPTED))
                .thenReturn(false);

        mentorshipRequestService.accept(requestId);

        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
        verify(mentorshipRequestRepository).save(request);
    }

    @Test
    void accept_throwsExceptionIfAlreadyMentor() {
        long userId = 2L;
        long requestId = 10L;

        when(userContext.getUserId()).thenReturn(userId);

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(userId);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(mentorshipRequestRepository.existsByRequesterIdAndReceiverIdAndStatus(requester.getId(),
                userId, RequestStatus.ACCEPTED))
                .thenReturn(true);

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.accept(requestId);
        });

        assertTrue(ex.getMessage().contains("уже является ментором"));
    }

    @Test
    void accept_throwsExceptionIfRequestNotPending() {
        long userId = 2L;
        long requestId = 10L;

        when(userContext.getUserId()).thenReturn(userId);

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(userId);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        DataValidationException ex = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.accept(requestId);
        });

        assertTrue(ex.getMessage().contains("только для запросов со статусом PENDING"));
    }

    @Test
    void accept_throwsExceptionIfNotReceiver() {
        long currentUserId = 3L;
        long requestId = 10L;

        when(userContext.getUserId()).thenReturn(currentUserId);

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L); // текущий пользователь не совпадает с получателем

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(ForbiddenException.class, () -> {
            mentorshipRequestService.accept(requestId);
        });

        verify(mentorshipRequestRepository, never()).save(any());
    }


    @Test
    void reject_successful() {
        long requestId = 1L;
        String reason = "Причина отказа";

        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(userContext.getUserId()).thenReturn(receiver.getId());
        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        mentorshipRequestService.reject(requestId, new RejectionDto(reason));

        assertEquals(RequestStatus.REJECTED, request.getStatus());
        // Можно проверить, что причина где-то сохранилась, если есть поле, например:
        // assertEquals(reason, request.getRejectionReason());

        verify(mentorshipRequestRepository).save(request);
    }

    @Test
    void reject_throwsExceptionIfReasonIsBlank() {
        long requestId = 1L;
        long currentUserId = 100L;

        RejectionDto rejectionDto = new RejectionDto(""); // пустая причина

        // Подготовка запроса
        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.PENDING);

        // Обязательный requester
        User requester = new User();
        requester.setId(200L);
        request.setRequester(requester);

        // Обязательный receiver = текущий пользователь
        User receiver = new User();
        receiver.setId(currentUserId);
        request.setReceiver(receiver);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(currentUserId); // эмуляция текущего пользователя

        // Проверка, что выбрасывается исключение
        assertThrows(DataValidationException.class,
                () -> mentorshipRequestService.reject(requestId, rejectionDto));

        verify(mentorshipRequestRepository).findById(requestId);
    }

    @Test
    void reject_throwsExceptionIfRequestNotFound() {
        long requestId = 1L;
        RejectionDto rejectionDto = new RejectionDto("Причина отказа");

        when(mentorshipRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> mentorshipRequestService.reject(requestId, rejectionDto));

        verify(mentorshipRequestRepository).findById(requestId);
    }
    @Test
    void reject_throwsExceptionIfNotReceiver() {
        long requestId = 1L;
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        MentorshipRequest request = new MentorshipRequest();
        request.setId(requestId);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userContext.getUserId()).thenReturn(3L); // Не совпадает с receiver

        assertThrows(ForbiddenException.class, () -> {
            mentorshipRequestService.reject(requestId, new RejectionDto("Причина"));
        });

        verify(mentorshipRequestRepository, never()).save(any());
    }
}