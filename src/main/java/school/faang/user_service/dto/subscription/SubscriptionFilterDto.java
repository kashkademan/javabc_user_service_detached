package school.faang.user_service.dto.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionFilterDto {
    @NotBlank
    private String namePattern;
    @NotBlank
    private String phonePattern;
    @NotNull
    private int experienceMin;
    @NotNull
    private int experienceMax;
}
