package school.faang.user_service.aspect.score;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScoreActionType {
    COMPLETE_GOAL(10),
    COMPLETE_EVENT(2);

    private final int defaultScore;
}
