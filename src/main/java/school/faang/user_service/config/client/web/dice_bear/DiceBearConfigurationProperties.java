package school.faang.user_service.config.client.web.dice_bear;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "services.dice-bear")
@Data
@Component
public class DiceBearConfigurationProperties {
    private String url;
    private String version;
}