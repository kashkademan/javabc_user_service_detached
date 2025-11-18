package school.faang.user_service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEventDto;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationReceivedEventPublisherTest {
    private static final long DEFAULT_ID = 1L;
    private static final long REQUESTER_ID = 2L;

    private final long requestId = DEFAULT_ID;
    private final long requesterId = REQUESTER_ID;
    private final long receiverId = DEFAULT_ID;

    private final RecommendationReceivedEventDto event =
            RecommendationReceivedEventDto.builder()
                    .receiverId(receiverId)
                    .authorId(requesterId)
                    .id(requestId)
                    .createdAt(LocalDateTime.now())
                    .build();

    @Captor
    private ArgumentCaptor<RecommendationReceivedEventDto> recommendationReceivedEventDtoArgumentCaptor =
            ArgumentCaptor.forClass(RecommendationReceivedEventDto.class);

    @Mock
    private KafkaTemplate<String, RecommendationReceivedEventDto> kafkaTemplate;

    @InjectMocks
    private RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;


    @Test
    void testPublish() {
        CompletableFuture<SendResult<String, RecommendationReceivedEventDto>> future =
                CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(
                eq("recommendation-received-events"),
                any(RecommendationReceivedEventDto.class))
        ).thenReturn(future);

        recommendationReceivedEventPublisher.publish(
                event.authorId(), event.receiverId(), event.id(), LocalDateTime.now()
        );

        verify(kafkaTemplate).send(
                eq("recommendation-received-events"),
                recommendationReceivedEventDtoArgumentCaptor.capture()
        );

        RecommendationReceivedEventDto capturedEvent = recommendationReceivedEventDtoArgumentCaptor.getValue();
        assertEquals(event.id(), capturedEvent.id());
    }
}