package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDto {
    private long id;
    private Long parentId;
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long mentorId;
    private List<Long> invitationsIds;
    private List<Long> usersIds;
    private List<Long> skillToAchieveIds;
}
