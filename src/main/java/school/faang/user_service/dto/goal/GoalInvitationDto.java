package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record GoalInvitationDto(
        @NotNull
        Long id,
        @NotNull
        UserDto invited,
        @NotNull
        RequestStatus status,
        @NotNull
        Long goalId
) {
}
