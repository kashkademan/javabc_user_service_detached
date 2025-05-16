package school.faang.user_service.entity.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GoalFilterDto(
        List<Long> usersId,
        String title,
        GoalStatus status,
        List<Long> skillsId,
        LocalDateTime deadline
) {
}