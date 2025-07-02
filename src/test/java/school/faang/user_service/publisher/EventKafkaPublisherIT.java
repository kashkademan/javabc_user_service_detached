package school.faang.user_service.publisher;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.TestPropertySource;
import school.faang.user_service.config.TestContainersConfig;
import school.faang.user_service.config.kafka.KafkaEventTopicProperties;
import school.faang.user_service.dto.event.EventResponseDto;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@TestPropertySource(properties = {
        "spring.kafka.topic.event.name=test-events"
})
@EnableConfigurationProperties(KafkaEventTopicProperties.class)
class EventKafkaPublisherIT extends TestContainersConfig {

    private static final String TOPIC = "test-events";

    @Autowired
    private EventKafkaPublisher publisher;

    private Consumer<String, EventResponseDto> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);

        JsonDeserializer<EventResponseDto> deserializer = new JsonDeserializer<>(EventResponseDto.class, false);
        deserializer.addTrustedPackages("*");

        consumer = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        ).createConsumer();

        consumer.subscribe(Collections.singletonList(TOPIC));
        consumer.poll(Duration.ofMillis(100));
        consumer.seekToBeginning(consumer.assignment());
    }

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldPublishEventToKafka() {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(123L);
        dto.setTitle("Kafka Integration Test");
        dto.setDescription("Testing via Testcontainers");

        publisher.sendMessage(dto);

        Awaitility.await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted(() -> {
                ConsumerRecords<String, EventResponseDto> records = consumer.poll(Duration.ofMillis(500));
                assertThat(records.count()).isGreaterThan(0);

                EventResponseDto received = records.iterator().next().value();
                assertThat(received.getId()).isEqualTo(123L);
                assertThat(received.getTitle()).isEqualTo("Kafka Integration Test");
            });
    }
}
