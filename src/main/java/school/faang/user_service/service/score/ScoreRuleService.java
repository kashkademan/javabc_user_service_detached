package school.faang.user_service.service.score;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.score.ScoreRule;
import school.faang.user_service.repository.score.ScoreRuleRepository;

@Service
@RequiredArgsConstructor
public class ScoreRuleService {

    private final ScoreRuleRepository scoreRuleRepository;

    public int getScoreByType(ScoreActionType type) {
        ScoreRule scoreRule = scoreRuleRepository.findByType(type);
        return scoreRule.getScore();
    }

    public int getScoreByRole(ScoreActionType type, String role) {
        ScoreRule scoreRule = scoreRuleRepository.findByTypeAndRole_Name(type, role);
        return scoreRule.getScore();
    }
}
