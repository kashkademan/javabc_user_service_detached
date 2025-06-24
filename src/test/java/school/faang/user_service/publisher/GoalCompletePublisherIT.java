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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GoalCompletePublisherIT {

    @Autowired
    private KafkaTemplate<String, GoalCompletionNotificationEvent> kafkaTemplate;

    @Autowired
    private EventListener eventListener;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.topics.test-topic.name", () -> "goal-completed");
    }

    @AfterEach
    void afterTest() {
        eventListener.setReceivedMessage(null);
    }

    @Test
    void testAnalyticsEventPublisher_UserExist() throws IOException {
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