package school.faang.user_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum GoalAction {
    CREATE_GOAL("createGoal", "Create Goal"),
    SUB_GOAL("subGoal", "Create Sub Goal"),
    UPDATE_GOAL("updateGoal", "Update Goal"),
    ;
    private final String code;
    private final String description;

    public static boolean in(GoalAction needle, GoalAction... targets) {
        return Arrays.stream(targets).anyMatch(target -> needle == target);
    }
}
