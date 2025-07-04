package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RejectionDto {
    @NotBlank(message = "Rejection reason is required")
    private String reason;
}