package school.faang.user_service.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "goal")
public record GoalProperty(@DefaultValue("10") int minParticipantsCount,
                           @DefaultValue("20") int activeGoalsLimit) {
}
