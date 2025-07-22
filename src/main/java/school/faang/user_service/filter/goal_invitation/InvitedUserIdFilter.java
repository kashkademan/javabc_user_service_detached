package school.faang.user_service.filter.goal_invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class InvitedUserIdFilter implements GoalInvitationFilter {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInvitedUserId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(inv ->
                Objects.equals(inv.getInvited().getId(), filter.getInvitedUserId())
        );
    }
}
