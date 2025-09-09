package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.publish.GoalCompletedEventDto;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
    private final RedisConfigurationProperties redisConfigurationProperties;
    private final ObjectMapper objectMapper;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                redisConfigurationProperties.getHost(),
                redisConfigurationProperties.getPort());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean(value = "goalCompletedTopic")
    ChannelTopic goalCompletedTopic(@Value("${spring.data.redis.channel.goalCompleted}") String goalCompletedChannel)
    {
        return new ChannelTopic(goalCompletedChannel);
    }

    @Bean
    public RedisTemplate<String, GoalCompletedEventDto> goalCompletedRedisTemplate
            (JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, GoalCompletedEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, GoalCompletedEventDto.class));
        return template;
    }
}
