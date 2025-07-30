package school.faang.user_service.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.temporal.ChronoUnit;

@ConfigurationProperties(prefix = "recommendation-request.once-every")
public record RecommendationRequestProperty(
        @DefaultValue("6") int quantity,
        @DefaultValue("MONTHS") ChronoUnit period
) {
}
