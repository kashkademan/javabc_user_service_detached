package school.faang.user_service.config.score;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import school.faang.user_service.aspect.score.ScoreActionType;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "score.rules")
@Getter
@Setter
public class ScoreRuleProperties {
    private Map<String, Integer> defaultValues = new HashMap<>();
    private Map<String, Map<String, Integer>> roleValues = new HashMap<>();

    public int getScore(ScoreActionType type, String role) {
        return roleValues.getOrDefault(type.name(), Map.of())
                .getOrDefault(role.toUpperCase(), 0);
    }

    public int getDefaultScore(ScoreActionType type) {
        return defaultValues.getOrDefault(type.name(), 0);
    }
}

