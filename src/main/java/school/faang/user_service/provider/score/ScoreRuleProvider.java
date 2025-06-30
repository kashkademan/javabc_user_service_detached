package school.faang.user_service.provider.score;

import school.faang.user_service.aspect.score.ScoreActionType;

public interface ScoreRuleProvider {
    int getScore(ScoreActionType type);
    int getScoreByRole(ScoreActionType type, String role);
}
