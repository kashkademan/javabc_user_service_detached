package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.goal.Goal;

@Getter
@RequiredArgsConstructor
public class CreateGoalInvitationDto {
    private final Long goalId;
    private final Long invitedUserId;
}
