package school.faang.user_service.entity.goal.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateGoalDto(
        @Schema(description = "Название цели", example = "Цель", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String title,
        @Schema(description = "Описание цели", example = "Описание цели")
        String description,
        @Schema(description = "ID цели родителя", example = "2")
        Long parentId,
        @Schema(description = "Список ID связанных с целью навыков", example = "[1, 2, 3]")
        @NotNull
        List<Long> skillsId,
        @Schema(description = "Срок достижения цели", example = "2026-06-01T15:00:00")
        @Future LocalDateTime deadline
) {}