package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
public class GoalFilterDto {
    private String description;
    private String title;
    private Long skill;
    private GoalStatus status;
}
