package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/goals/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public ResponseEntity<GoalInvitationDto> createInvitation(@RequestBody GoalInvitationCreateDto createDto) {
        GoalInvitationDto created = goalInvitationService.createInvitation(createDto);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptGoalInvitation(@PathVariable long id) {
        goalInvitationService.acceptGoalInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectGoalInvitation(@PathVariable long id) {
        goalInvitationService.rejectGoalInvitation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getInvitations(@ModelAttribute InvitationFilterDto filter) {
        List<GoalInvitationDto> invitations = goalInvitationService.getInvitations(filter);
        return ResponseEntity.ok(invitations);
    }
}
