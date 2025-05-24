package school.faang.user_service.dto.goal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateGoalDto(
        @Schema(description = "Название цели", example = "Цель")
        @NotBlank(message = "Empty goal title not allowed!")
        String title,
        @Schema(description = "Описание цели", example = "Описание цели")
        String description,
        @Schema(description = "Статус цели", example = "ACTIVE")
        GoalStatus status,
        @Schema(description = "Список ID связанных с целью навыков", example = "[1, 2, 3]")
        @NotNull
        List<Long> skillsId,
        @Schema(description = "Срок достижения цели", example = "2026-06-01T15:00:00")
        @Future(message = "Dead line must be in future!") LocalDateTime deadline
) {}