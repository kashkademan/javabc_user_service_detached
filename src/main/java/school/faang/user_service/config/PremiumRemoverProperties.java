package school.faang.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "premium.remover")
@Getter
@Setter
public class PremiumRemoverProperties {
    private int batchSize;
    private String cron;
}
