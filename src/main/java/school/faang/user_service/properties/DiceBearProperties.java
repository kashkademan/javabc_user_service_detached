package school.faang.user_service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "clients.dice-bear-client")
public record DiceBearProperties(
        String host,
        String apiVersion,
        String styleName,
        String format,
        int size
) {
}