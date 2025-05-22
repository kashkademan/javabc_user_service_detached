package school.faang.user_service.entity.goal.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalDto(
        Long id,
        String title,
        String description,
        Long parentId,
        GoalStatus status,
        List<Long> skillsId,
        LocalDateTime deadline
) {}