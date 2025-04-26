package school.faang.user_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.messaging.RedisEventPublisher;

@Configuration
public class RedisConfig {

    @Value("${data.redis.host}")
    private String redisHost;

    @Value("${data.redis.port}")
    private Integer redisPort;

    @Value("${data.redis.channel.follower_channel}")
    private String followEventsTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

    @Bean
    public ChannelTopic followEventTopic() {
        return new ChannelTopic(followEventsTopic);
    }


    @Bean
    public EventPublisher<FollowEventDto> followEventPublisher(
            RedisTemplate<String, Object> redisTemplate, ChannelTopic followEventTopic) {
        return new RedisEventPublisher<>(redisTemplate, followEventTopic);
    }
}
