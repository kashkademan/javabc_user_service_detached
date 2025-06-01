package school.faang.user_service.service.test;

import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertAll;
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

    private RecommendationMapper recommendationMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private User requester1;
    private User requester2;
    private User receiver1;
    private LocalDateTime now;
    private RecommendationRequest request1;
    private RecommendationRequest request2;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        requester1 = new User();
        requester1.setId(1L);

        requester2 = new User();
        requester2.setId(2L);

        receiver1 = new User();
        receiver1.setId(3L);

        request1 = RecommendationRequest.builder()
                .id(1L)
                .requester(requester1)
                .receiver(receiver1)
                .message("Java developer needed")
                .status(RequestStatus.PENDING)
                .createdAt(now.minusDays(1))
                .build();

        request2 = RecommendationRequest.builder()
                .id(2L)
                .requester(requester2)
                .receiver(receiver1)
                .message("Python position available")
                .status(RequestStatus.ACCEPTED)
                .createdAt(now.minusHours(2))
                .build();
    }

    @Test
    void create_ValidRequest_ReturnsResponseDto() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title",
                "Description",
                List.of("Java", "Spring"),
                requester1.getId(),
                receiver1.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        RecommendationRequest savedRequest = new RecommendationRequest();
        savedRequest.setId(1L);
        savedRequest.setRequester(requester1);
        savedRequest.setReceiver(receiver1);
        savedRequest.setStatus(RequestStatus.PENDING);
        savedRequest.setMessage("Message");

        when(userRepository.findById(requester1.getId())).thenReturn(Optional.of(requester1));
        when(userRepository.findById(receiver1.getId())).thenReturn(Optional.of(receiver1));
        when(recommendationRequestRepository.findLatestPendingRequest(requester1.getId(), receiver1.getId()))
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
                List.of("Java", "Spring"),
                requester1.getId(),
                receiver1.getId(),
                savedRequest.getCreatedAt(),
                savedRequest.getUpdatedAt()
        );

        when(recommendationMapper.toDto(savedRequest)).thenReturn(expectedDto);

        RecommendationResponseDto result = recommendationRequestService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(requester1.getId(), result.requesterId());
        assertEquals(receiver1.getId(), result.receiverId());
        assertEquals(List.of("Java", "Spring"), result.skills());
        verify(recommendationRequestRepository).save(any());
        verify(skillRequestRepository, times(2)).save(any());
    }

    @Test
    void create_RecentRequestExists_ThrowsException() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMonthsAgo = now.minusMonths(5);

        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title",
                "Description",
                List.of("Java", "Spring"),
                requester1.getId(),
                receiver1.getId(),
                now,
                now
        );

        RecommendationRequest recentRequest = new RecommendationRequest();
        recentRequest.setCreatedAt(fiveMonthsAgo);

        when(userRepository.findById(requester1.getId())).thenReturn(Optional.of(requester1));
        when(userRepository.findById(receiver1.getId())).thenReturn(Optional.of(receiver1));
        when(recommendationRequestRepository.findLatestPendingRequest(requester1.getId(), receiver1.getId()))
                .thenReturn(Optional.of(recentRequest));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> recommendationRequestService.create(requestDto));

        assertAll(
                () -> assertTrue(exception.getMessage().contains("send a recommendation request"),
                        "Сообщение должно содержать текст о запросе рекомендации"),
                () -> assertTrue(exception.getMessage().contains("only once per 6 months"),
                        "Сообщение должно содержать информацию о 6-месячном интервале")
        );
    }

    @Test
    void create_RequesterEqualsReceiver_ThrowsException() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title", "Desc", List.of("Java"), requester1.getId(), requester1.getId(), null, null
        );

        assertThrows(DataValidationException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void getRequests_NoFilters_ReturnsAllRequests() {
        RequestFilterDto filter = new RequestFilterDto(null, null, null, null, null, null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(any(RecommendationRequest.class)))
                .thenAnswer(inv -> recommendationMapper.toDto(inv.getArgument(0)));

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
        when(recommendationMapper.toDto(request1)).thenReturn(recommendationMapper.toDto(request1));

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
        when(recommendationMapper.toDto(any())).thenAnswer(inv -> recommendationMapper.toDto(inv.getArgument(0)));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.receiverId() == 3L));
    }

    @Test
    void getRequests_FilterByMessagePattern_ReturnsFiltered() {

        RequestFilterDto filter = new RequestFilterDto(null, null, null, "Java", null, null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(request1)).thenReturn(recommendationMapper.toDto(request1));

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
        when(recommendationMapper.toDto(request2)).thenReturn(recommendationMapper.toDto(request2));

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

}
