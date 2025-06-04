package school.faang.user_service.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationMapperImpl recommendationMapper;
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
    private static final String JAVA = "Java";
    private static final String SPRING = "Spring";



    @BeforeEach
    void setUp() {

        now = LocalDateTime.now();
        requester1 = User.builder().id(1L).build();
        requester2 = User.builder().id(2L).build();
        receiver1 = User.builder().id(3L).build();

        request1 = createRequest(
                1L,
                requester1,
                receiver1,
                "Java developer needed",
                RequestStatus.PENDING,
                now.minusDays(1)
        );

        request2 = createRequest(
                2L,
                requester2,
                receiver1,
                "Python position available",
                RequestStatus.ACCEPTED,
                now.minusHours(2)
        );

    }

    @Test
    void create_ValidRequest_ReturnsResponseDto() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto(
                "Title",
                "Description",
                List.of(JAVA, SPRING),
                requester1.getId(),
                receiver1.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        RecommendationRequest savedRequest = createRequest(
                1L,
                requester1,
                receiver1,
                "Message",
                RequestStatus.PENDING,
                null
        );

        when(userRepository.findById(requester1.getId())).thenReturn(Optional.of(requester1));
        when(userRepository.findById(receiver1.getId())).thenReturn(Optional.of(receiver1));
        when(recommendationRequestRepository.findLatestPendingRequest(requester1.getId(), receiver1.getId()))
                .thenReturn(Optional.empty());
        when(recommendationRequestRepository.save(any())).thenReturn(savedRequest);


        Skill javaSkill = Skill.builder().id(1L).title(JAVA).build();
        Skill springSkill = Skill.builder().id(2L).title(SPRING).build();

        when(skillRepository.findByTitle("Java")).thenReturn(Optional.of(javaSkill));
        when(skillRepository.findByTitle("Spring")).thenReturn(Optional.of(springSkill));

        RecommendationResponseDto expectedDto = new RecommendationResponseDto(
                1L,
                "Title",
                "Description",
                List.of(JAVA, SPRING),
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
        assertEquals(List.of(JAVA, SPRING), result.skills());
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
                List.of(JAVA, SPRING),
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
                "Title",
                "Desc",
                List.of(JAVA),
                requester1.getId(),
                requester1.getId(),
                null,
                null
        );

        assertThrows(DataValidationException.class,
                () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void getRequests_NoFilters_ReturnsAllRequests() {
        RequestFilterDto filter = new RequestFilterDto(null,
                null,
                null,
                null,
                null,
                null);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(2, result.size());
        verify(recommendationRequestRepository).findAll();
    }


    @Test
    void getRequests_FilterByRequesterId_ReturnsFiltered() {
        Long targetRequesterId = 1L;

        RecommendationRequest request1 = createRequest(1L, requester1, receiver1,
                "Java developer needed", RequestStatus.PENDING, now.minusDays(1));
        RecommendationRequest request2 = createRequest(2L, requester2, receiver1,
                "Python position available", RequestStatus.ACCEPTED, now.minusHours(2));

        RequestFilterDto filter = new RequestFilterDto(
                targetRequesterId,
                null,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.requesterId()).isEqualTo(targetRequesterId);
                    assertThat(dto.receiverId()).isEqualTo(receiver1.getId());
                    assertThat(dto.message()).isEqualTo(request1.getMessage());
                    assertThat(dto.status()).isEqualTo("PENDING");
                    assertThat(dto.createdAt()).isEqualTo(request1.getCreatedAt());
                });
    }

    @Test
    void getRequests_FilterByReceiverId_ReturnsFiltered() {

        LocalDateTime now = LocalDateTime.now();
        RecommendationRequest request1 = createRequest(1L, requester1, receiver1,
                "Java developer needed", RequestStatus.PENDING, now.minusDays(1));
        RecommendationRequest request2 = createRequest(2L, requester2, receiver1,
                "Python position available", RequestStatus.ACCEPTED, now.minusHours(2));

        RequestFilterDto filter = new RequestFilterDto(
                null,
                3L,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));


        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.receiverId() == 3L));
    }

    @Test
    void getRequests_FilterByMessagePattern_ReturnsFiltered() {

        RequestFilterDto filter = new RequestFilterDto(
                null,
                null,
                null,
                "Java",
                null,
                null
        );

        RecommendationResponseDto expectedDto = new RecommendationResponseDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));
        when(recommendationMapper.toDto(request1)).thenReturn(expectedDto);

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertEquals(1, result.size());
        assertEquals(expectedDto, result.get(0));
        verify(recommendationRequestRepository).findAll();

        verify(recommendationMapper, times(2)).toDto(request1);
        verify(recommendationMapper, never()).toDto(request2);
    }

    @Test
    void getRequests_CombinedFilters_ReturnsCorrectlyFiltered() {

        RequestFilterDto filter = new RequestFilterDto(
                2L,
                null,
                null,
                "Python",
                null,
                null
        );

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request1, request2));

        List<RecommendationResponseDto> result = recommendationRequestService.getRequests(filter);

        assertThat(result)
                .hasSize(1)
                .first()
                .satisfies(dto -> {
                    assertThat(dto.requesterId()).isEqualTo(2L);
                    assertThat(dto.message()).contains("Python");
                });
    }

    @Test
    void getRequests_NoMatchingFilters_ReturnsEmptyList() {

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

    @Test
    void rejectRequest_ValidRequest_RejectsAndReturnsDto() {

        long requestId = 1L;
        String rejectionReason = "Not qualified";
        RecommendationRejectDto rejectDto = new RecommendationRejectDto(rejectionReason);

        RecommendationRequest request = createRequest(
                requestId,
                requester1,
                receiver1,
                "Message",
                RequestStatus.PENDING,
                now
        );

        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecommendationResponseDto result = recommendationRequestService.rejectRequest(requestId, rejectDto);

        assertAll(
                () -> assertEquals(requestId, result.id()),
                () -> assertEquals(RequestStatus.REJECTED.toString(), result.status()),
                () -> assertEquals(rejectionReason, request.getRejectionReason()),
                () -> verify(recommendationRequestRepository).findById(requestId),
                () -> verify(recommendationRequestRepository).save(request)
        );
    }

    @Test
    void rejectRequest_RequestNotFound_ThrowsException() {
        long nonExistentId = 999L;
        RecommendationRejectDto rejectDto = new RecommendationRejectDto("Reason");

        when(recommendationRequestRepository.findById(nonExistentId))
                .thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> recommendationRequestService.rejectRequest(nonExistentId, rejectDto));

        verify(recommendationRequestRepository).findById(nonExistentId);
        verify(recommendationRequestRepository, never()).save(any());
    }

    @Test
    void rejectRequest_NotPendingStatus_ThrowsException() {
        long requestId = 2L;
        RecommendationRejectDto rejectDto = new RecommendationRejectDto("Reason");

        RecommendationRequest request = createRequest(
                requestId,
                requester1,
                receiver1,
                "Message",
                RequestStatus.ACCEPTED,
                now
        );

        when(recommendationRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.rejectRequest(requestId, rejectDto));

        assertTrue(exception.getMessage().contains("Cannot reject request"));
        assertTrue(exception.getMessage().contains(RequestStatus.ACCEPTED.toString()));

        verify(recommendationRequestRepository).findById(requestId);
        verify(recommendationRequestRepository, never()).save(any());
    }

    private RecommendationRequest createRequest(
            Long id,
            User requester,
            User receiver,
            String message,
            RequestStatus status,
            LocalDateTime createdAt
    ) {
        return RecommendationRequest.builder()
                .id(id)
                .requester(requester)
                .receiver(receiver)
                .message(message)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
