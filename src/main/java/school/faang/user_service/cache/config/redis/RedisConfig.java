package school.faang.user_service.cache.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.messaging.dto.ProfileVisitEvent;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

/**
 * Конфигурация Spring для подключения и настройки Redis.
 * <p>
 * Определяет бин {@link StringRedisTemplate}, который используется для
 * операций с Redis с ключами и значениями типа {@code String}.
 * <p>
 * {@link RedisConnectionFactory} автоматически подставляется Spring Boot
 * на основании настроек подключения к Redis.
 */
@Configuration
public class RedisConfig {

    /**
     * Создаёт и настраивает бин {@link StringRedisTemplate} для работы с Redis.
     *
     * @param factory фабрика подключения к Redis
     * @return настроенный {@link StringRedisTemplate}
     */
    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private <T> RedisTemplate<String, T> buildTemplate(
            RedisConnectionFactory factory,
            ObjectMapper mapper,
            Class<T> clazz
    ) {
        var template = new RedisTemplate<String, T>();
        template.setConnectionFactory(factory);

        var serializer = new Jackson2JsonRedisSerializer<>(mapper, clazz);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        return template;
    }

    @Bean
    public RedisTemplate<String, SearchAppearanceEvent> searchAppearanceEventRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper
    ) {
        return buildTemplate(factory, redisObjectMapper, SearchAppearanceEvent.class);
    }

    @Bean
    public RedisTemplate<String, ProfileVisitEvent> profileVisitEventRedisTemplate(
            RedisConnectionFactory factory,
            ObjectMapper redisObjectMapper
    ) {
        return buildTemplate(factory, redisObjectMapper, ProfileVisitEvent.class);
    }
}
