package school.faang.user_service.controller.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import school.faang.user_service.event.FollowerEvent;
import school.faang.user_service.publisher.FollowerEventPublisher;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class FollowerEventPublisherIntegrationTest {

    @Autowired
    private FollowerEventPublisher eventPublisher;

    @Autowired
    private JedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ChannelTopic followerTopic;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
        registry.add("spring.data.redis.host", () -> redisContainer.getHost());
    }

    @Test
    void shouldPublishEventToRedisChannel() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedPayload = new AtomicReference<>();

        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(redisConnectionFactory);

        listenerContainer.addMessageListener((message, pattern) -> {
            receivedPayload.set(new String(message.getBody(), StandardCharsets.UTF_8));
            latch.countDown();
        }, followerTopic);

        listenerContainer.afterPropertiesSet();
        listenerContainer.start();

        try {
            Awaitility.await()
                    .atMost(5, TimeUnit.SECONDS)
                    .until(listenerContainer::isRunning);

            FollowerEvent event = new FollowerEvent(2L, 1L, LocalDateTime.now());
            eventPublisher.publish(event);

            boolean received = latch.await(5, TimeUnit.SECONDS);
            assertTrue(received, "redis message did not arrive");

            String jsonMessage = receivedPayload.get();
            assertNotNull(jsonMessage);

            FollowerEvent deserializedMessage =
                    objectMapper.readValue(jsonMessage, FollowerEvent.class);

            assertEquals(2L, deserializedMessage.followeeId());
            assertEquals(1L, deserializedMessage.followerId());
            assertNotNull(deserializedMessage.timestamp());
        } finally {
            listenerContainer.stop();
            listenerContainer.destroy();
        }
    }
}
