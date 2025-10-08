package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(Long id,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime createdAt,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime updatedAt,
                      String title,
                      String description,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                      LocalDateTime deadline,
                      Long mentorId,
                      List<Long> userIds,
                      GoalStatus status,
                      Long parentGoalId
) {
}
