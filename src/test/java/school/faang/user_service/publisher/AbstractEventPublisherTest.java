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
import school.faang.user_service.exception.EventPublishingException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.publisher.AbstractEventPublisherTestData.NAME_TOPIC;

@ExtendWith(MockitoExtension.class)
public class AbstractEventPublisherTest {

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private TestEventPublisher eventPublisher;
    private RecommendationEventPublisher event;

    @BeforeEach
    public void setUp() {
        eventPublisher = new TestEventPublisher(retryTemplate, redisTemplate);
        RecommendationEventPublisher event = new RecommendationEventPublisher(retryTemplate, redisTemplate);
        ReflectionTestUtils.setField(eventPublisher, "topic", NAME_TOPIC);
    }

    @Test
    @DisplayName("Проверка на вызов метода, когда слушатели у топика существуют")
    public void testPublish_WhenSubscriberIsExist() {
        when(redisTemplate.convertAndSend(anyString(), any())).thenReturn(1L);

        eventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(NAME_TOPIC, event);
    }

    @Test
    @DisplayName("Проверка на вызов метода, когда слушатели у топика отсутствуют")
    public void testPublish_WhenSubscriberIsZero() {
        when(redisTemplate.convertAndSend(anyString(), any())).thenReturn(0L);

        eventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(NAME_TOPIC, event);
    }

    @Test
    @DisplayName("Проверяет, что ловится исключение при ошибке отправки ивента в топик")
    public void testPublish_WhenException() {
        RuntimeException redisException = new RuntimeException("Redis error");

        when(redisTemplate.convertAndSend(eq(NAME_TOPIC), eq(event))).thenThrow(redisException);

        EventPublishingException exception = assertThrows(EventPublishingException.class,
                () -> eventPublisher.publish(event));

        verify(redisTemplate).convertAndSend(NAME_TOPIC, event);

        assertSame(exception.getCause(), redisException);
        assertTrue(exception.getMessage().contains("Ошибка отправки ивента в Redis"));
    }
}