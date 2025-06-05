package school.faang.user_service.dto.goal.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GoalFilterDto(
        @Schema(description = "Фильтр по пользователям", example = "[1, 2, 3]")
        List<Long> usersId,
        @Schema(description = "Фильтр по названию", example = "Цель")
        String title,
        @Schema(description = "Фильтр по статусу", example = "COMPLETE")
        GoalStatus status,
        @Schema(description = "Фильтр по навыкам", example = "[1, 2, 3]")
        List<Long> skillsId,
        @Schema(description = "Фильтр по времени выполнения", example = "2026-06-01T15:00:00")
        LocalDateTime deadline
) {
}