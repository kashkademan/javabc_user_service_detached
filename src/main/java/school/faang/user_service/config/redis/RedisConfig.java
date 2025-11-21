package school.faang.user_service.config.redis;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import school.faang.user_service.messages.redis.listeners.UsersBanListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Valid
    private final RedisProperties redisProperties;
    private final Map<ChannelTopic, MessageListenerAdapter> containersAdapter = new HashMap<>();

    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        if (StringUtils.hasText(redisProperties.getPassword())) {
            config.setPassword(RedisPassword.of(redisProperties.getPassword()));
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ChannelTopic userBanTopic(@Value("${redis.topics.name.user-ban-topic}") String topicName) {
        return new ChannelTopic(topicName);
    }

    @Bean
    public MessageListenerAdapter userBanListenerAdapter(UsersBanListener usersBanListener, ChannelTopic userBanTopic) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(usersBanListener);
        containersAdapter.put(userBanTopic, adapter);
        return adapter;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        containersAdapter.forEach((key, value) ->
                container.addMessageListener(value, key));
        return container;
    }

    @Bean
    public ChannelTopic mentorshipOfferedTopic(@Value("${redis.topics.name.mentorship-offered}") String topicName) {
        return new ChannelTopic(topicName);
    }
}