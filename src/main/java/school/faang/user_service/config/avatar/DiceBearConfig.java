package school.faang.user_service.config.avatar;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dicebear")
public class DiceBearConfig {

    private String apiUrl = "https://api.dicebear.com/9.x";
    private String style = "pixel-art";
    private String defaultSeed = "defaultUserSeed";
}