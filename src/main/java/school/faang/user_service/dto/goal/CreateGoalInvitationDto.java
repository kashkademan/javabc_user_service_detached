package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;

public record CreateGoalInvitationDto(
        @NotNull
        Long invitedUserId,
        @NotNull
        Long goalId
) {

}
