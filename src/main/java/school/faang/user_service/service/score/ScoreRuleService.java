package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.config.score.ScoreRuleProperties;

@Service
@RequiredArgsConstructor
public class ScoreRuleService {

    private static final String ATTENDEE_ROLE = "ATTENDEE";
    private static final String OWNER_ROLE = "OWNER";

    private final ScoreRuleProperties properties;

    public int getDefaultScore(ScoreActionType type) {
        return properties.getDefaultScore(type);
    }

    public int getScoreForRole(ScoreActionType type, String role) {
        return properties.getScore(type, role);
    }

    public int getParticipationScore(ScoreActionType type) {
        return getScoreForRole(type, ATTENDEE_ROLE);
    }

    public int getOwnerScore(ScoreActionType type) {
        return getScoreForRole(type, OWNER_ROLE);
    }
}
