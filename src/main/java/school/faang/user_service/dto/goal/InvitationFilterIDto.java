package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationFilterIDto {
    private Long inviterId;
    private Long invitedId;
    private RequestStatus status;
}
