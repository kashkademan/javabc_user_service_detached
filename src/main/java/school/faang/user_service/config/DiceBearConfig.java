package school.faang.user_service.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "dicebear")
public class DiceBearConfig {

    @NotBlank
    private String apiUrl;

    @NotBlank
    private String style;
}
