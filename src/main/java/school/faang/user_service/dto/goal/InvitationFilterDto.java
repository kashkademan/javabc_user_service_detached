package school.faang.user_service.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class InvitationFilterDto {
    private Long inviterId;
    private Long invitedUserId;
    private RequestStatus status;
}
