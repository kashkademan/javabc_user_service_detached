package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RejectionDto {
    @NotBlank(message = "Rejection reason cannot be blank")
    @Size(max = 4096, message = "Rejection reason must be less than 4096 characters")
    private String reason;
}