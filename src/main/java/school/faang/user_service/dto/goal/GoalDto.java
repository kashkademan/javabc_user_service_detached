package school.faang.user_service.dto.goal;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
public class GoalDto {
    private Long id;
    private String description;
    private Long parent;
    @NotEmpty
    private String title;
    private GoalStatus status;
    private List<Long> skillsToAchie;
    private List<Long> users;
}
