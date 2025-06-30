package school.faang.user_service.provider.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.aspect.score.ScoreActionType;

@Component
@RequiredArgsConstructor
public class RedisScoreRuleProvider implements ScoreRuleProvider {

    private final ScoreRuleCache cache;

    @Override
    public int getScore(ScoreActionType type) {
        return cache.getScore(type.name());
    }

    @Override
    public int getScoreByRole(ScoreActionType type, String role) {
        return cache.getScoreByRole(type.name(), role);
    }
}
