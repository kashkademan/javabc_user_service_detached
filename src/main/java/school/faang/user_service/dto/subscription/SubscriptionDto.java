package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String username;
    @NotBlank
    private String email;
}
