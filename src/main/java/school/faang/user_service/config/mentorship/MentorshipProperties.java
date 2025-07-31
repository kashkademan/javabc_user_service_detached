package school.faang.user_service.config.mentorship;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "mentorship")
public record MentorshipProperties(
        @DefaultValue("3") int monthsToSubtract
) {
}
