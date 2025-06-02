package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
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
@ConfigurationPropertiesScan
public class RedisConfiguration {
    private final RedisParam redisParams;

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
        config.setHostName(redisParams.host());
        config.setPort(redisParams.port());

        return new JedisConnectionFactory(config);
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(redisParams.channels().userBan());
    }

    @Bean
    public MessageListenerAdapter userBanListener(UserBanSubscriber userBanSubscriber) {
        return new MessageListenerAdapter(userBanSubscriber);
    }
}
