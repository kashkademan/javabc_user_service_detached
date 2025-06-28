package school.faang.user_service.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Component
public class InviterFilter implements InvitationFilter {
    @Override
    public boolean doFilter(GoalInvitation goalInvitation, InvitationFilterIDto criteria) {
        return goalInvitation.getInviter().getId().equals(criteria.getInviterId());
    }

    @Override
    public boolean isApplicable(InvitationFilterIDto criteria) {
        return criteria.getInviterId() != null;
    }
}
