package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGoalInvitationDto {
    @NotNull(message = "Inviter ID cannot be null")
    private Long invitedUserId;
}
