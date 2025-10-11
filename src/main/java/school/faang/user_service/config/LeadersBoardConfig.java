package school.faang.user_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.ActionType;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "leaders-board")
@Component
public class LeadersBoardConfig {
    private Map<String, Integer> scores = new HashMap<>();

    public int getPointsFor(ActionType actionType) {
        String key = actionType.name().toLowerCase();
        return scores.getOrDefault(key, 0);
    }
}
