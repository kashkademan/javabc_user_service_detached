package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import school.faang.user_service.subscriber.UserBanSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisProperties redisProperties;

    @Bean
    public RedisMessageListenerContainer redisContainer(MessageListenerAdapter userBanListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(userBanListener, topic());
        return container;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.host());
        config.setPort(redisProperties.port());

        return new JedisConnectionFactory(config);
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(redisProperties.channels().userBan());
    }

    @Bean
    public MessageListenerAdapter userBanListener(UserBanSubscriber userBanSubscriber) {
        return new MessageListenerAdapter(userBanSubscriber);
    }
}
