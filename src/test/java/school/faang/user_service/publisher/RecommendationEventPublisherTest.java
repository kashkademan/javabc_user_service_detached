package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.exception.EventPublishingException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static school.faang.user_service.publisher.AbstractEventPublisherTestData.NAME_TOPIC;

/**
 * Тестирование класс для публикации ивентов рекомендации
 */

@DisplayName("Тестирование RecommendationEventPublisher")
@ExtendWith(MockitoExtension.class)
class RecommendationEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private RecommendationEventPublisher publisher;

    private RecommendationEvent testEvent;

    @BeforeEach
    void setUp() {
        publisher = new RecommendationEventPublisher(retryTemplate, redisTemplate);
        ReflectionTestUtils.setField(publisher, "topic", NAME_TOPIC);

        testEvent = new RecommendationEvent(
                1L,
                2L,
                100L,
                null
        );
    }

    @Test
    @DisplayName("Успешное выполнение метода publish")
    void testPublish_Success() {
        Long expectedReceiversCount = 5L;

        when(redisTemplate.convertAndSend(NAME_TOPIC, testEvent))
                .thenReturn(expectedReceiversCount);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(NAME_TOPIC, testEvent);
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    @DisplayName("Проверка на успешное выполнение, когда число 'подписчиков' топика 0")
    void testPublish_ZeroReceivers() {
        when(redisTemplate.convertAndSend(eq(NAME_TOPIC), eq(testEvent)))
                .thenReturn(0L);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(NAME_TOPIC, testEvent);
    }

    @Test
    @DisplayName("Проверка на успешное выполнение, когда число 'подписчиков' топика null")
    void testPublish_NullReceivers() {
        when(redisTemplate.convertAndSend(eq(NAME_TOPIC), eq(testEvent)))
                .thenReturn(null);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(NAME_TOPIC, testEvent);
    }

    @Test
    @DisplayName("Ошибка в соединении с Redis")
    void testPublish_RedisException() {
        String errorMessage = "Ошибка в соединении с Redis";
        RuntimeException redisException = new EventPublishingException(errorMessage);

        doThrow(redisException)
                .when(redisTemplate)
                .convertAndSend(eq(NAME_TOPIC), eq(testEvent));

        EventPublishingException exception = assertThrows(
                EventPublishingException.class,
                () -> publisher.publish(testEvent)
        );

        assertEquals("Ошибка отправки ивента в Redis", exception.getMessage());
        assertSame(redisException, exception.getCause());

        verify(redisTemplate, times(1))
                .convertAndSend(NAME_TOPIC, testEvent);
    }

    @Test
    @DisplayName("Переданный event является Null")
    void testPublish_WithNullEvent() {
        when(redisTemplate.convertAndSend(eq(NAME_TOPIC), isNull()))
                .thenReturn(0L);

        assertDoesNotThrow(() -> publisher.publish(null));

        verify(redisTemplate, times(1))
                .convertAndSend(NAME_TOPIC, null);
    }

    @Test
    @DisplayName("Топик не был указан (пустая строка)")
    void testPublish_EmptyTopic() {
        ReflectionTestUtils.setField(publisher, "topic", "");
        when(redisTemplate.convertAndSend(eq(""), eq(testEvent)))
                .thenReturn(null);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend("", testEvent);
    }
}