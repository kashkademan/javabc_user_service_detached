package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(Long id,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt,
                      String title,
                      String description,
                      LocalDateTime deadline,
                      Long mentorId,
                      List<Long> userIds,
                      GoalStatus status,
                      Long parentGoalId
) {
}
