package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.service.user.UsersBanListener;

@Configuration
public class RedisConfig {
    @Value("${redis.topics.listener.user-ban-topic}")
    private String USER_BAN_TOPIC;
    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;
    @Value("${spring.data.redis.port}")
    private int REDIS_PORT;
    @Value("${spring.data.redis.password}")
    private String REDIS_PASSWORD;

    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(REDIS_HOST);
        config.setPort(REDIS_PORT);
        if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
            config.setPassword(RedisPassword.of(REDIS_PASSWORD));
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(USER_BAN_TOPIC);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter messageListenerAdapter,
                                                        ChannelTopic userBanTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, userBanTopic);
        return container;
    }

    @Bean
    public MessageListenerAdapter redisListenerAdapter(UsersBanListener usersBanListener) {
        return new MessageListenerAdapter(usersBanListener, "onMessage");
    }
}