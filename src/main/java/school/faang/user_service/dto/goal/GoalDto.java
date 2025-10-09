package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import school.faang.user_service.entity.goal.GoalStatus;

@Getter
@AllArgsConstructor
public class GoalDto {
    private final Long id;
    private final String title;
    private final GoalStatus status;
    private final String description;
}
