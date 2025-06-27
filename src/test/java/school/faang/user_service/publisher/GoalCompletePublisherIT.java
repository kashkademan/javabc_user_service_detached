package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import school.faang.user_service.config.EventListener;
import school.faang.user_service.dto.notification.GoalCompletionNotificationEvent;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(EventListener.class)
@Testcontainers
@SpringBootTest
public class GoalCompletePublisherIT {

    @Autowired
    private KafkaTemplate<String, GoalCompletionNotificationEvent> kafkaTemplate;

    @Autowired
    private EventListener eventListener;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void afterTest() {
        eventListener.setReceivedMessage(null);
    }

    @Test
    void testPublishCompletedGoalSuccess() throws IOException {
        GoalCompletionNotificationEvent expectedEvent = GoalCompletionNotificationEvent.builder()
                .goalTitle("test")
                .build();

        kafkaTemplate.send("goal-completed", expectedEvent);

        await()
                .pollInterval( 2, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertNotNull(eventListener.getReceivedMessage());

                    GoalCompletionNotificationEvent actualEvent = eventListener.getReceivedMessage();

                    assertEquals(expectedEvent.getGoalTitle(), actualEvent.getGoalTitle());
                });
    }
}