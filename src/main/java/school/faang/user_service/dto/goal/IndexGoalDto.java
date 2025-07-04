package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;

public record IndexGoalDto(
        @Nullable
        Long page,
        @Nullable
        Long totalPage,
        @Nullable
        FilterGoalDto filters
) {
}
