package school.faang.user_service.validator.goal.request;

import school.faang.user_service.enums.GoalAction;
import school.faang.user_service.util.Util;

import java.io.Serializable;

public record GoalRequestValidationParams(String path, GoalAction action) implements Serializable {
    public String path(String path) {
        return Util.createPath(this.path, path);
    }
}
