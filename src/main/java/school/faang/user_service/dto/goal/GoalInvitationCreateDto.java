package school.faang.user_service.dto.goal;

import lombok.Data;

@Data
public class GoalInvitationCreateDto {
    private Long inviterId;
    private Long invitedUserId;
    private Long goalId;
}
