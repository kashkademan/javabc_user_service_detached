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
import school.faang.user_service.dto.recommendation.RecommendationEvent;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование {@link RecommendationRequestedEventPublisher} класса отправляющего ивент в топик Redis
 *
 * @author Linempy
 * @since 22.08.2025
 */
@ExtendWith(MockitoExtension.class)
public class RecommendationEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecommendationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new RecommendationEventPublisher(redisTemplate, objectMapper);
        ReflectionTestUtils.setField(
                publisher,
                "recommendationTopic",
                "test-topic"
        );
    }

    @Test
    @DisplayName("Успешная преобразование в json и отправка в топик")
    public void publishSendsCorrectMessage() throws JsonProcessingException {
        RecommendationEvent event = new RecommendationEvent(
                1L,
                2L,
                1L,
                LocalDateTime.now()
        );
        String expectedJson = "{\"requesterId\":1,\"receiverId\":2, \"requestId\":1}";

        when(objectMapper.writeValueAsString(event)).thenReturn(expectedJson);

        publisher.publish(event);

        verify(redisTemplate).convertAndSend("test-topic", expectedJson);
    }

    @Test
    @DisplayName("Ожидание ошибки конвертации в json")
    public void publishShouldThrowRuntimeExceptionOnSerializationError() throws JsonProcessingException {
        RecommendationEvent event = new RecommendationEvent(
                1L,
                2L,
                1L,
                LocalDateTime.now()
        );
        when(objectMapper.writeValueAsString(event))
                .thenThrow(new JsonProcessingException("Error") {});

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }

}