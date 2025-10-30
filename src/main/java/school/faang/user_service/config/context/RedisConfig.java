package school.faang.user_service.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.entity.redis.RedisPromotionEntity;

@Configuration
public class RedisConfig {

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

    @Bean
    public RedisTemplate<String, RedisPromotionEntity> redisPromotionTemplate(
            LettuceConnectionFactory redisConnectionFactory,
            ObjectMapper objectMapper) {

        RedisTemplate<String, RedisPromotionEntity> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(stringRedisSerializer);

        var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, RedisPromotionEntity.class);

        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setDefaultSerializer(serializer);

        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
