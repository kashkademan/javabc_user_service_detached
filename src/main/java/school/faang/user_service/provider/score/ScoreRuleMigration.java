package school.faang.user_service.provider.score;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.repository.score.ScoreRuleRepository;

@Component
@RequiredArgsConstructor
public class ScoreRuleMigration {

    private final ScoreRuleRepository repository;

    @PostConstruct
    public void init() {
        repository.setScore(ScoreActionType.COMPLETE_GOAL.name(), 10);
        repository.setScoreByRole(ScoreActionType.COMPLETE_EVENT.name(), "OWNER", 20);
        repository.setScoreByRole(ScoreActionType.COMPLETE_EVENT.name(), "ATTENDEE", 5);
    }
}
