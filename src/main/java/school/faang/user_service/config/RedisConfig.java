package school.faang.user_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.dto.AchievementEventDto;
import school.faang.user_service.dto.event.FollowEventDto;
import school.faang.user_service.messaging.EventPublisher;
import school.faang.user_service.messaging.RedisEventPublisher;

@Configuration
public class RedisConfig {

    String achievementEventsTopic = "achievement_topic";
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private Integer redisPort;
    @Value("${spring.data.redis.channel.follower}")
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
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }


    @Bean
    public ChannelTopic followEventTopic() {
        return new ChannelTopic(followEventsTopic);
    }

    @Bean
    public ChannelTopic AchievementEventTopic() {
        return new ChannelTopic(achievementEventsTopic);
    }


    @Bean
    public EventPublisher<FollowEventDto> followEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ChannelTopic followEventTopic
    ) {
        return new RedisEventPublisher<>(redisTemplate, followEventTopic);
    }

    @Bean
    public EventPublisher<AchievementEventDto> AchievementEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ChannelTopic followEventTopic
    ) {
        return new RedisEventPublisher<>(redisTemplate, followEventTopic);
    }
}
