package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserFilterDto (
        @NotBlank(message = "Field cannot be blank")
        @Size(max = 255, message = "Field length must be less or equal 255")
        String namePattern,

        @NotBlank(message = "Field cannot be blank")
        @Size(max = 255, message = "Field length must be less or equal 255")
        String phonePattern,

        @NotNull(message = "Field cannot be null")
        Integer experienceMin,

        @NotNull(message = "Field cannot be null")
        Integer experienceMax) {}
