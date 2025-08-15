package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование класса отправляющего ивент в топик Redis
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
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecommendationRequestedEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new RecommendationRequestedEventPublisher(redisTemplate, objectMapper);
        ReflectionTestUtils.setField(
                publisher,
                "recommendationRequestTopic",
                "test-topic"
        );
    }

    @Test
    @DisplayName("Успешная преобразование в json и отправка в топик")
    public void publishSendsCorrectMessage() throws JsonProcessingException {
        RecommendationRequestedEvent event = new RecommendationRequestedEvent(1L, 2L, 1L);
        String expectedJson = "{\"requesterId\":1,\"receiverId\":2, \"requestId\":1}";

        when(objectMapper.writeValueAsString(event)).thenReturn(expectedJson);

        publisher.publish(event);

        verify(redisTemplate).convertAndSend("test-topic", expectedJson);
    }

    @Test
    @DisplayName("Ожидание ошибки конвертации в json")
    public void publishShouldThrowRuntimeExceptionOnSerializationError() throws JsonProcessingException {
        RecommendationRequestedEvent event = new RecommendationRequestedEvent(1L, 2L, 1L);
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("Error") {});

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }
}