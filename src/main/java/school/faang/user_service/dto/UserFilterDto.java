package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserFilterDto (
        @NotNull(message = "Field cannot be null")
        String namePattern,

        @NotNull(message = "Field cannot be null")
        String phonePattern,

        @NotNull(message = "Field cannot be null")
        Integer experienceMin,

        @NotNull(message = "Field cannot be null")
        Integer experienceMax) {}
