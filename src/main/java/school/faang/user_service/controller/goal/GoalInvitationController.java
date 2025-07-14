package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@RequiredArgsConstructor
@Controller
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;

    public void createInvitation(GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }
}
