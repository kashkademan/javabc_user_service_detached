package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "premium.remover")
@Data
public class PremiumRemoverProperties {

    private String cron;
    private int batchSize;
    private int poolSize;
}
