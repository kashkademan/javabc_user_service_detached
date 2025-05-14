package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalFilterDto {

    private String description;
    private String title;
    private GoalStatus status;
    private List<String> skillTitles;
    private LocalDateTime createdBefore;
    private LocalDateTime createdAfter;
    private LocalDateTime updatedBefore;
    private LocalDateTime updatedAfter;
}
