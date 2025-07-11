package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(
        Long id,
        Long parentId,
        String title,
        String description,
        GoalStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime deadline,
        Long mentorId,
        List<Long> userIds
) {
}
