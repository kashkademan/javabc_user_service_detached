package school.faang.user_service.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import school.faang.user_service.config.properties.AuthorRequestTopicProperties;
import school.faang.user_service.config.properties.AuthorResponseTopicProperties;
import school.faang.user_service.config.properties.FeedUsersResponseTopicProperties;
import school.faang.user_service.config.properties.FeedWarmupTopicProperties;
import school.faang.user_service.config.properties.FollowersRequestTopicProperties;
import school.faang.user_service.config.properties.PostsTopicProperties;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final FollowersRequestTopicProperties followersRequestTopic;
    private final PostsTopicProperties postsTopic;
    private final AuthorRequestTopicProperties authorRequestTopic;
    private final AuthorResponseTopicProperties authorResponseTopic;
    private final FeedUsersResponseTopicProperties feedUsersResponseTopic;
    private final FeedWarmupTopicProperties feedWarmupTopic;

    @Bean
    public NewTopic followersRequestTopic() {
        return createTopic(followersRequestTopic.name(),
                followersRequestTopic.partitions(),
                followersRequestTopic.replicas());
    }

    @Bean
    public NewTopic postsTopic() {
        return createTopic(postsTopic.name(),
                postsTopic.partitions(),
                postsTopic.replicas());
    }

    @Bean
    public NewTopic authorRequestTopic() {
        return createTopic(authorRequestTopic.name(),
                authorRequestTopic.partitions(),
                authorRequestTopic.replicas());
    }

    @Bean
    public NewTopic authorResponseTopic() {
        return createTopic(authorResponseTopic.name(),
                authorResponseTopic.partitions(),
                authorResponseTopic.replicas());
    }

    @Bean
    public NewTopic feedUsersResponseTopic() {
        return createTopic(feedUsersResponseTopic.name(),
                feedUsersResponseTopic.partitions(),
                feedUsersResponseTopic.replicas());
    }

    @Bean
    public NewTopic feedWarmupTopic() {
        return createTopic(feedWarmupTopic.name(),
                feedWarmupTopic.partitions(),
                feedWarmupTopic.replicas());
    }

    private NewTopic createTopic(String name, int partitions, int replicas) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
