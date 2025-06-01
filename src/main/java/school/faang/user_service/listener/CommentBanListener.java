package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.ConvertingDataException;
import school.faang.user_service.exception.UserBanProcessingException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentBanListener {

    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Transactional
    @KafkaListener(topics = "${spring.data.kafka.topic.userBan}", groupId = "${spring.data.kafka.consumer.group-id}")
    void onMessage(String message, Acknowledgment ack) {
        try {
            List<Long> usersForBan = objectMapper.readValue(message, new TypeReference<>() {});
            userRepository.banUsers(usersForBan);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            throw new ConvertingDataException("Error processing message: Unable to convert Message: {}", e, message);
        } catch (Exception e) {
            throw new UserBanProcessingException("Failed to process message to ban users", e);
        }
    }
}
