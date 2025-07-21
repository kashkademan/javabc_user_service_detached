package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/goals/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService goalInvitationService;


    @PostMapping
    public void createInvitation(@RequestBody GoalInvitationDto invitation) {
        goalInvitationService.createInvitation(invitation);
    }

    @PostMapping("/{id}/accept")
    public void acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }

    @PostMapping("/{id}/reject")
    public void rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
    }

    @GetMapping
    public List<GoalInvitationDto> getInvitations(@ModelAttribute InvitationFilterDto filter) {
        return goalInvitationService.getInvitations(filter);
    }
}
