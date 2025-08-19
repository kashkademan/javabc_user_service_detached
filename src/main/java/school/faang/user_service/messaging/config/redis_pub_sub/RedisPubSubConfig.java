package school.faang.user_service.messaging.config.redis_pub_sub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.messaging.consumer.redis_pub_sub.SearchAppearanceEventConsumer;

/**
 * RedisConfig — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Configuration
public class RedisPubSubConfig {
    @Value("${kafka.topics.search-appearance}")
    private String searchAppearanceTopic;

    @Bean
    public MessageListenerAdapter messageListener(MessageListener subscriber) {
        return new MessageListenerAdapter(subscriber);
    }

    @Bean
    public RedisMessageListenerContainer redisSearchAppearanceEventContainer(RedisConnectionFactory connectionFactory) {
        var container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(new SearchAppearanceEventConsumer(), searchAppearanceTopic());
        return container;
    }

    @Bean
    public ChannelTopic searchAppearanceTopic() {
        return new ChannelTopic(searchAppearanceTopic);
    }
}
