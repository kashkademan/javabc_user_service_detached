package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDto {

    private Long id;

    @NotBlank
    @Size(max = 64, message = "Название цели не должно быть больше 64 символов!")
    private String title;

    @NotBlank
    @Size(max = 128, message = "Описание не должно быть больше 128 символов!")
    private String description;

    private LocalDateTime deadline;

    private Long mentorId;

    private List<Long> userIds;

    private GoalStatus status;

    private LocalDateTime createdAt;

    private List<Long> skillsToAchieveIds;
}