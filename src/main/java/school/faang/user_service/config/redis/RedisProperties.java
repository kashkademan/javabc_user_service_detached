package school.faang.user_service.config.redis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("spring.redis")
@Configuration
public class RedisProperties {
    @NotBlank(message = "Redis host not be null")
    private String host;
    @Positive(message = "Redis port cannot be negative")
    private int port;
    @NotBlank(message = "Redis password not be null")
    private String password;
}