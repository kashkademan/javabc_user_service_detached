package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {
    private final static long DEFAULT_ID = 1L;
    private final static long REQUESTER_ID = 2L;
    private final static long INCORRECT_ID = 123L;


    @InjectMocks
    RecommendationRequestServiceImpl recommendationRequestService;
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;
    @Mock
    UserContext userContext;


    private final long requestId = DEFAULT_ID;
    private final long requesterId = REQUESTER_ID;
    private final long receiverId = DEFAULT_ID;
    private final long incorrectUserId = INCORRECT_ID;

    private User requesterUser = User.builder()
            .id(requesterId)
            .build();

    private User receiverUser = User.builder()
            .id(receiverId)
            .build();


    private RecommendationRequest recommendationRequest = RecommendationRequest.builder()
            .id(requestId)
            .requester(requesterUser)
            .receiver(receiverUser)
            .status(RequestStatus.PENDING)
            .build();
    @Test
    public void testSuccessfullyRecommendationRequestAcceptPublished() {
        when(recommendationRequestRepository.getByIdOrThrow(requestId)).thenReturn(recommendationRequest);
        when(userContext.getUserId()).thenReturn(receiverId);

        recommendationRequestService.accept(requestId);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(recommendationReceivedEventPublisher, times(1)).publish(any(Long.class),
                any(Long.class),
                any(Long.class),
                any(LocalDateTime.class));
    }

    @Test
    public void testFailedRecommendationRequestAcceptPublishing() {
        when(recommendationRequestRepository.getByIdOrThrow(requestId)).thenReturn(recommendationRequest);
        when(userContext.getUserId()).thenReturn(incorrectUserId);

        Assertions.assertThrows(ForbiddenException.class,
                () -> recommendationRequestService.accept(requestId));

        verify(recommendationRequestRepository, never()).save(recommendationRequest);
        verify(recommendationReceivedEventPublisher, never()).publish(any(Long.class),
                any(Long.class),
                any(Long.class),
                any(LocalDateTime.class));
    }
}
