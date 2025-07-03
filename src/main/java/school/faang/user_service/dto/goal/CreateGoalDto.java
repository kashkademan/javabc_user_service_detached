package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CreateGoalDto(

        @NotBlank(message = "Введите заголовок цели")
        String title,
        @NotBlank(message = "Введите описание цели")
        String description,

        @Future(message = "Некорректная дата дедлайна")
        @NotNull(message = "Введите дедлайн задачи")
        LocalDateTime deadline,

        Long mentorId,

        @NotEmpty(message = "Не заданы пользователи цели")
        List<Long> userIds,

        @Nullable
        List<Long> skillIds
) {
}
