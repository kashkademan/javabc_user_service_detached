package school.faang.user_service.validator.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.util.Util;

import java.io.Serializable;

public record GoalValidationParams(Long userId, GoalDto goalRq, String path) implements Serializable {
    public String path(String path) {
        return Util.createPath(this.path, path);
    }
}
