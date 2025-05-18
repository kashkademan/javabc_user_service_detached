package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenteeDto {

    @NonNull
    private Long userId;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}

