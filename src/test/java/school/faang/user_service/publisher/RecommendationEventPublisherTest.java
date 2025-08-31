package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.exception.EventPublishingException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Тестирование класс для публикации ивентов рекомендации
 */

@DisplayName("Тестирование RecommendationEventPublisher")
@ExtendWith(MockitoExtension.class)
class RecommendationEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private RecommendationEventPublisher publisher;

    private final String testTopic = "test-recommendation-topic";
    private RecommendationEvent testEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "recommendationTopic", testTopic);

        testEvent = new RecommendationEvent(
                1L,
                2L,
                100L,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Успешное выполнение метода publish")
    void testPublish_Success() {
        Long expectedReceiversCount = 5L;

        when(redisTemplate.convertAndSend(eq(testTopic), eq(testEvent)))
                .thenReturn(expectedReceiversCount);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(testTopic, testEvent);
        verifyNoMoreInteractions(redisTemplate);
    }

    @Test
    @DisplayName("Проверка на успешное выполнение, когда число 'подписчиков' топика 0")
    void testPublish_ZeroReceivers() {
        when(redisTemplate.convertAndSend(eq(testTopic), eq(testEvent)))
                .thenReturn(0L);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(testTopic, testEvent);
    }

    @Test
    @DisplayName("Проверка на успешное выполнение, когда число 'подписчиков' топика null")
    void testPublish_NullReceivers() {
        when(redisTemplate.convertAndSend(eq(testTopic), eq(testEvent)))
                .thenReturn(null);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend(testTopic, testEvent);
    }

    @Test
    @DisplayName("Ошибка в соединении с Redis")
    void testPublish_RedisException() {
        String errorMessage = "Ошибка в соединении с Redis";
        RuntimeException redisException = new RuntimeException(errorMessage);

        when(redisTemplate.convertAndSend(anyString(), any()))
                .thenThrow(redisException);

        EventPublishingException exception = assertThrows(
                EventPublishingException.class,
                () -> publisher.publish(testEvent)
        );

        assertEquals("Ошибка отправки ивента в Redis", exception.getMessage());
        assertSame(redisException, exception.getCause());

        verify(redisTemplate, times(1))
                .convertAndSend(testTopic, testEvent);
    }

    @Test
    @DisplayName("Переданный event является Null")
    void testPublish_WithNullEvent() {
        when(redisTemplate.convertAndSend(eq(testTopic), isNull()))
                .thenReturn(0L);

        assertDoesNotThrow(() -> publisher.publish(null));

        verify(redisTemplate, times(1))
                .convertAndSend(testTopic, null);
    }

    @Test
    @DisplayName("Топик не был указан (пустая строка)")
    void testPublish_EmptyTopic() {
        ReflectionTestUtils.setField(publisher, "recommendationTopic", "");

        when(redisTemplate.convertAndSend(eq(""), eq(testEvent)))
                .thenReturn(1L);

        assertDoesNotThrow(() -> publisher.publish(testEvent));

        verify(redisTemplate, times(1))
                .convertAndSend("", testEvent);
    }
}