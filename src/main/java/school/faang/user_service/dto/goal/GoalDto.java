package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;
import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(Long id,
                      Long parentId,
                      String title,
                      String description,
                      LocalDateTime createdAt,
                      LocalDateTime deadline,
                      LocalDateTime updatedAt,
                      Long mentorId,
                      List<Long> userIds,
                      GoalStatus status
) {
}
