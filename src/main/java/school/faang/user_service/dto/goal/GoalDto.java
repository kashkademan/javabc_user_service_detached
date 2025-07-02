package school.faang.user_service.dto.goal;

import java.util.List;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
public class GoalDto {
    private Long id;
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status;
    private List<Long> skillIds;
}
