package school.faang.user_service.service.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private final User requester1 = new User();
    private final User requester2 = new User();
    private final User receiver1 = new User();
    private final LocalDateTime now = LocalDateTime.now();

    private final RecommendationRequest request1 = RecommendationRequest.builder()
            .id(1L)
            .requester(requester1)
            .receiver(receiver1)
            .message("Java developer needed")
            .status(RequestStatus.PENDING)
            .createdAt(now.minusDays(1))
            .build();

    private final RecommendationRequest request2 = RecommendationRequest.builder()
            .id(2L)
            .requester(requester2)
            .receiver(receiver1)
            .message("Python position available")
            .status(RequestStatus.ACCEPTED)
            .createdAt(now.minusHours(2))
            .build();

    @Test
    void create_ValidRequest_ReturnsResponseDto() {

        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = List.of("Java", "Spring");

        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title",
                "Description",
                skills,
                requesterId,
                receiverId,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);

        RecommendationRequest savedRequest = new RecommendationRequest();
        savedRequest.setId(1L);
        savedRequest.setRequester(requester);
        savedRequest.setReceiver(receiver);
        savedRequest.setStatus(RequestStatus.PENDING);
        savedRequest.setMessage("Message");

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());
        when(recommendationMapper.toEntity(requestDto)).thenReturn(new RecommendationRequest());
        when(recommendationRequestRepository.save(any())).thenReturn(savedRequest);

        Skill javaSkill = new Skill();
        javaSkill.setId(1L);
        javaSkill.setTitle("Java");

        Skill springSkill = new Skill();
        springSkill.setId(2L);
        springSkill.setTitle("Spring");

        when(skillRepository.findByTitle("Java")).thenReturn(Optional.of(javaSkill));
        when(skillRepository.findByTitle("Spring")).thenReturn(Optional.of(springSkill));

        RecommendationResponseDto expectedDto = new RecommendationResponseDto(
                1L,
                "Title",
                "Description",
                skills,
                requesterId,
                receiverId,
                savedRequest.getCreatedAt(),
                savedRequest.getUpdatedAt()
        );

        when(recommendationMapper.toDto(savedRequest)).thenReturn(expectedDto);

        RecommendationResponseDto result = recommendationRequestService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(requesterId, result.requesterId());
        assertEquals(receiverId, result.receiverId());
        assertEquals(skills, result.skills());
        verify(recommendationRequestRepository, times(1)).save(any());
        verify(skillRequestRepository, times(2)).save(any());
        assertEquals(RequestStatus.PENDING, savedRequest.getStatus());
        assertEquals("Message", savedRequest.getMessage());
        verify(recommendationRequestRepository, times(1)).save(any());
        verify(skillRequestRepository, times(2)).save(any());
        verify(skillRepository, times(2)).findByTitle(any());
    }

    @Test
    void create_RequesterEqualsReceiver_ThrowsException() {

        Long userId = 1L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title", "Desc", List.of("Java"), userId, userId, null, null
        );

        assertThrows(DataValidationException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void create_RequesterNotFound_ThrowsException() {

        Long userId = 1L;
        Long user2Id = 2L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title", "Desc", List.of("Java"), userId, user2Id, null, null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void create_ReceiverNotFound_ThrowsException() {

        Long requesterId = 1L;
        Long receiverId = 2L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Message", "PENDING", List.of("Java"), requesterId, receiverId, null, null
        );

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void create_RecentRequestExists_ThrowsException() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto("Message", "PENDING", List.of("Java"), 1L, 2L, null, null);

        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);

        RecommendationRequest recentRequest = new RecommendationRequest();
        recentRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.of(recentRequest));

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void create_EmptySkillsList_ThrowsException() {

        Long requesterId = 1L;
        Long receiverId = 2L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto("Message", "PENDING", List.of(), 1L, 2L, null, null);

        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void create_SkillNotFound_ThrowsException() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        String skillTitle = "NonExistingSkill";
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Message", "PENDING", List.of(skillTitle), requesterId, receiverId, null, null
        );

        User requester = new User();
        requester.setId(requesterId);
        User receiver = new User();
        receiver.setId(receiverId);

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId))
                .thenReturn(Optional.empty());
        when(recommendationMapper.toEntity(requestDto)).thenReturn(new RecommendationRequest());
        when(recommendationRequestRepository.save(any())).thenReturn(new RecommendationRequest());
        when(skillRepository.findByTitle(skillTitle)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }
    @Test
    void getRequests_NoFilters_ReturnsAllRequests() {
        RequestFilterDto filter = new RequestFilterDto(null, null, null, null, null, null);
        List<RecommendationRequest> allRequests = List.of(request1, request2);

        when(recommendationRequestRepository.findAll()).thenReturn(allRequests);
        when(recommendationMapper.toDto(any(RecommendationRequest.class)))
                .thenAnswer(inv -> mapToDto(inv.getArgument(0)));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(2, result.size());
        verify(recommendationRequestRepository).findAll();
    }

    @Test
    void getRequests_FilterByRequesterId_ReturnsFiltered() {
        User requester1 = new User();
        requester1.setId(1L);

        User requester2 = new User();
        requester2.setId(2L);

        User receiver = new User();
        receiver.setId(3L);

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester1)
                .receiver(receiver)
                .message("Java developer needed")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester2)
                .receiver(receiver)
                .message("Python position available")
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        RequestFilterDto filter = new RequestFilterDto(
                1L,
                null,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(request1)).thenReturn(mapToDto(request1));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).requesterId());
    }

    @Test
    void getRequests_FilterByReceiverId_ReturnsFiltered() {

        User requester1 = new User();
        requester1.setId(1L);

        User requester2 = new User();
        requester2.setId(2L);

        User receiver = new User();
        receiver.setId(3L);

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester1)
                .receiver(receiver)
                .message("Java developer needed")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester2)
                .receiver(receiver)
                .message("Python position available")
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        RequestFilterDto filter = new RequestFilterDto(
                null,
                3L,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(any())).thenAnswer(inv -> mapToDto(inv.getArgument(0)));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.receiverId() == 3L));
    }

    @Test
    void getRequests_FilterByMessagePattern_ReturnsFiltered() {

        RequestFilterDto filter = new RequestFilterDto(null, null, null, "Java", null, null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(request1)).thenReturn(mapToDto(request1));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertTrue(result.get(0).message().contains("Java"));
    }

    @Test
    void getRequests_CombinedFilters_ReturnsCorrectlyFiltered() {

        User requester1 = new User();
        requester1.setId(1L);

        User requester2 = new User();
        requester2.setId(2L);

        User receiver = new User();
        receiver.setId(3L);

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester1)
                .receiver(receiver)
                .message("Java developer needed")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester2)
                .receiver(receiver)
                .message("Python position available")
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        RequestFilterDto filter = new RequestFilterDto(
                2L,
                null,
                null,
                "Python",
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(request2)).thenReturn(mapToDto(request2));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).requesterId());
        assertTrue(result.get(0).message().contains("Python"));
    }

    @Test
    void getRequests_NoMatchingFilters_ReturnsEmptyList() {
        User requester1 = new User();
        requester1.setId(1L);

        User requester2 = new User();
        requester2.setId(2L);

        User receiver = new User();
        receiver.setId(3L);

        RecommendationRequest request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester1)
                .receiver(receiver)
                .message("Java developer needed")
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        RecommendationRequest request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester2)
                .receiver(receiver)
                .message("Python position available")
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        RequestFilterDto filter = new RequestFilterDto(
                99L,
                null,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertTrue(result.isEmpty());
    }

    private RecommendationResponseDto mapToDto(RecommendationRequest request) {
        return new RecommendationResponseDto(
                request.getId(),
                request.getMessage(),
                request.getStatus().toString(),
                List.of(),
                request.getRequester().getId(),
                request.getReceiver().getId(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
