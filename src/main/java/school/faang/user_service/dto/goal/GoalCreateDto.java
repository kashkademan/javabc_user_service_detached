package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record GoalCreateDto(
        Long parentId,
        @NotBlank
        @Size(max = 64, message = GoalConstant.TITLE_SIZE_NOT_VALID_MESSAGE)
        String title,
        @NotBlank
        @Size(max = 4096, message = GoalConstant.DESCRIPTION_SIZE_NOT_VALID_MESSAGE)
        String description,
        @NotNull
        @Future(message = GoalConstant.DEADLINE_NOT_VALID_MESSAGE)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime deadline,
        Long mentorId,
        @NotNull
        List<Long> userIds
) {
}
