package school.faang.user_service.messaging.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableAsync
public class CommonPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Qualifier("objectMapper")
    private final ObjectMapper mapper;

    @Async
    public void sendRedis(String topic, Object event) {
        redisTemplate.convertAndSend(topic, event);
    }

    @Async
    public void sendKafka(String topic, Object event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send(topic, String.valueOf(event.hashCode()), json);
        } catch (JsonProcessingException e) {
            log.error("Convert to json exception", e.getMessage(), e);
        }
    }
}
