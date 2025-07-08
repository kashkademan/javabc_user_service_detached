package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public class GoalDto {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private Long mentorId;
    private List<Long> userIds;
    private GoalStatus status;
}
