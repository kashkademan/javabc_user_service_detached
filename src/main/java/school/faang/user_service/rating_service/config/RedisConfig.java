package school.faang.user_service.rating_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

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
}
