package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Тестирование {@link RecommendationRequestedEventPublisher} класса отправляющего ивент в топик Redis
 *
 * @author Linempy
 * @since 15.08.2025
 */
@DisplayName("Тестирование RecommendationRequestedEventPublisher")
@ExtendWith(MockitoExtension.class)
public class RecommendationRequestedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    private RecommendationRequestedEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new RecommendationRequestedEventPublisher(retryTemplate, redisTemplate);
        ReflectionTestUtils.setField(publisher, "topic", "test-topic");
    }

    @Test
    @DisplayName("Успешная отправка события в топик")
    public void publishSendsCorrectMessage() {
        RecommendationRequestedEvent event = new RecommendationRequestedEvent(1L, 2L, 1L);

        publisher.publish(event);

        verify(redisTemplate).convertAndSend("test-topic", event);
    }

    @Test
    @DisplayName("Ожидание ошибки при отправке в Redis")
    public void publishShouldThrowRuntimeExceptionOnRedisError() {
        RecommendationRequestedEvent event = new RecommendationRequestedEvent(1L, 2L, 1L);

        doThrow(new RuntimeException("Redis error"))
                .when(redisTemplate)
                .convertAndSend("test-topic", event);

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }
}