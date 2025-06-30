package school.faang.user_service.config.score;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import school.faang.user_service.aspect.score.ScoreActionType;

import java.util.HashMap;
import java.util.Map;

@Component
public class ScoreRuleProperties {
    private final Map<String, Integer> scoreRules = new HashMap<>();
    private final Map<String, Map<String, Integer>> scoreRulesByRole = new HashMap<>();


    public ScoreRuleProperties() {
    }

    public int getScoreByRole(ScoreActionType type, String role) {
        return scoreRulesByRole.getOrDefault(type.name(), Map.of())
                .getOrDefault(role.toUpperCase(), 0);
    }

    public int getScore(ScoreActionType type) {
        return scoreRules.getOrDefault(type.name(), 0);
    }
}
