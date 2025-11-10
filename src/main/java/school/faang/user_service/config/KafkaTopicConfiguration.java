package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfiguration {

    private final KafkaTopicConfig kafkaTopicConfig;

    @Bean
    public NewTopic profileViewTopic() {
        return createTopic(kafkaTopicConfig.getAnalytics().getProfileView());
    }

    private NewTopic createTopic(String topicName) {
        return createTopic(
                topicName,
                kafkaTopicConfig.getDefaults().getPartitions(),
                kafkaTopicConfig.getDefaults().getReplicas()
        );
    }

    private NewTopic createTopic(String topicName, int partitions, int replicas) {
        NewTopic topic = TopicBuilder
                .name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();

        log.info("Configured Kafka topic: '{}' with {} partitions and {} replicas",
                topicName, partitions, replicas);

        return topic;
    }
}

