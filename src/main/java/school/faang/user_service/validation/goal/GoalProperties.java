package school.faang.user_service.validation.goal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "goal")
@Getter
@Setter
public class GoalProperties {
    private int maxLimit;
}