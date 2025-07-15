package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.recommendation_request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.PENDING;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceImplTest {
    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestFilter recommendationRequestMessageContainsFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestReceiverIdFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestRequesterIdFilter;

    @Mock
    private RecommendationRequestFilter recommendationRequestStatusFilter;

    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestService = new RecommendationRequestServiceImpl(
                recommendationRequestRepository,
                userRepository,
                recommendationRequestMapper,
                userContext,
                skillRequestRepository,
                Set.of(recommendationRequestMessageContainsFilter,
                        recommendationRequestReceiverIdFilter,
                        recommendationRequestRequesterIdFilter,
                        recommendationRequestStatusFilter)
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreate() {
    }

    @Test
    @DisplayName("Get all recommendation requests by filters")
    void testGetByFilters() {
        List<RecommendationRequest> recommendationRequests = List.of(
                RecommendationRequest.builder()
                        .requester(User.builder().id(1L).build())
                        .receiver(User.builder().id(2L).build())
                        .message("some message")
                        .status(PENDING)
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(2L).build())
                        .receiver(User.builder().id(3L).build())
                        .message("other message")
                        .status(ACCEPTED)
                        .build(),
                RecommendationRequest.builder()
                        .requester(User.builder().id(3L).build())
                        .receiver(User.builder().id(1L).build())
                        .message("another message")
                        .status(REJECTED)
                        .build()
        );

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);

        when(recommendationRequestMessageContainsFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestRequesterIdFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestReceiverIdFilter.isApplicable(any())).thenReturn(true);
        when(recommendationRequestStatusFilter.isApplicable(any())).thenReturn(true);

        when(recommendationRequestMessageContainsFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getMessage().toLowerCase().contains("Message".toLowerCase()));
                    }
                });

        when(recommendationRequestRequesterIdFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getRequester().getId().equals(3L));
                    }
                });

        when(recommendationRequestReceiverIdFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getReceiver().getId().equals(1L));
                    }
                });

        when(recommendationRequestStatusFilter.apply(any(), any()))
                .thenAnswer(new Answer<Stream<RecommendationRequest>>() {
                    @Override
                    public Stream<RecommendationRequest> answer(InvocationOnMock invocation) throws Throwable {
                        Stream<RecommendationRequest> recommendationRequestStream = invocation.getArgument(0);
                        return recommendationRequestStream.filter(recommendationRequest ->
                                recommendationRequest.getStatus().equals(REJECTED));
                    }
                });

        List<RecommendationRequestDto> result = recommendationRequestService
                .getByFilters(new RecommendationRequestFilterDto(
                        null,
                        null,
                        null,
                        null
                ));

        assertEquals(1, result.size());
    }


    @Test
    void testGetById() {
    }

    @Test
    void testAccept() {
    }

    @Test
    void testReject() {
    }
}