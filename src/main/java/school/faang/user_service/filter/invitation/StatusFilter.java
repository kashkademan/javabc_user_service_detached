package school.faang.user_service.filter.invitation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterIDto;
import school.faang.user_service.entity.goal.GoalInvitation;

@Component
public class StatusFilter implements InvitationFilter {
    @Override
    public boolean doFilter(GoalInvitation goalInvitation, InvitationFilterIDto criteria) {
        return goalInvitation.getStatus() == criteria.getStatus();
    }

    @Override
    public boolean isApplicable(InvitationFilterIDto criteria) {
        return criteria.getStatus() != null;
    }
}
