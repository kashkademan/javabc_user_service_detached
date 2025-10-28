package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RejectionDto(
        @NotBlank(message = "Reason must not be blank")
        String reason) {
}