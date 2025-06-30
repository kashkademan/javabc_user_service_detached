package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.provider.score.ScoreRuleProvider;

@Service
@RequiredArgsConstructor
public class ScoreRuleService {

    private static final String ATTENDEE_ROLE = "ATTENDEE";
    private static final String OWNER_ROLE = "OWNER";

    private final ScoreRuleProvider scoreRuleProvider;

    public int getScore(ScoreActionType type) {
        return scoreRuleProvider.getScore(type);
    }

    public int getParticipationScore(ScoreActionType type) {
        return getScoreByRole(type, ATTENDEE_ROLE);
    }

    public int getOwnerScore(ScoreActionType type) {
        return getScoreByRole(type, OWNER_ROLE);
    }

    private int getScoreByRole(ScoreActionType type, String role) {
        return scoreRuleProvider.getScoreByRole(type, role);
    }
}
