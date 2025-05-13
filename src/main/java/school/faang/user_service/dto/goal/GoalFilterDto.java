package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalFilterDto {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Completed is mandatory")
    private Boolean completed;
}
