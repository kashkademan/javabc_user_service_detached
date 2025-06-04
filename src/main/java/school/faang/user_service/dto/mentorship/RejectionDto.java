package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectionDto {

    @NotBlank
    @Size(max = 4096, message = "Причина отказа не может быть больше 1024 символов!")
    private String reason;
}