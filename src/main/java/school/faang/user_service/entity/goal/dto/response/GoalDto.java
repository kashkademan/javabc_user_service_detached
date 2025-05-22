package school.faang.user_service.entity.goal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record GoalDto(
        @Schema(description = "ID цели", example = "1")
        Long id,
        @Schema(description = "Название цели", example = "Цель")
        String title,
        @Schema(description = "Описание цели", example = "Описание цели")
        String description,
        @Schema(description = "ID цели родителя", example = "2")
        Long parentId,
        @Schema(description = "Статус цели", example = "ACTIVE")
        GoalStatus status,
        @Schema(description = "Список ID связанных с целью навыков", example = "[1, 2, 3]")
        List<Long> skillsId,
        @Schema(description = "Срок достижения цели", example = "2026-06-01T15:00:00")
        LocalDateTime deadline
) {}