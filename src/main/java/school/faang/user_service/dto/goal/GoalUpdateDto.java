package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record GoalUpdateDto(
        String title,
        String description,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime deadline,
        Long mentorId,
        GoalStatus status
) {
}
