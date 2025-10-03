package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalDto(Long id,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime createdAt,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime updatedAt,
                      String title,
                      String description,
                      @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime deadline,
                      Long mentorId,
                      List<Long> userIds,
                      GoalStatus status,
                      Long parentGoalId
) {
}
