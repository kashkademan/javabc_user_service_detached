package school.faang.user_service.helpers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import school.faang.user_service.config.kafka.KafkaTopicConfig;

@Slf4j
@Configuration
@RequiredArgsConstructor
//todo: заготовка для интеграционных тестов, топики не создаем бинами, только через баш скрипт в инфре
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic profileViewTopic() {
        return createTopic("topic name");
    }

    private NewTopic createTopic(String topicName) {
        return createTopic(
                topicName,
                3,
                1
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

