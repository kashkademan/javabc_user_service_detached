package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.RequestStatus;

public record GoalInvitationFilterDto(
        @NotNull
        Long invitedId,
        @NotNull
        RequestStatus status
) {
}
