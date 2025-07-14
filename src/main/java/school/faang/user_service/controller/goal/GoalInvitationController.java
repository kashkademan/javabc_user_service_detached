package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/{goalId}/invitations")
    public GoalInvitationDto create(@PathVariable long goalId,
                                    @Valid @RequestBody CreateGoalInvitationDto invitationDto) {

        return goalInvitationService.create(goalId, invitationDto);
    }

    @PutMapping("/invitations/{invitationId}/acceptance")
    public void accept(@PathVariable long invitationId) {
        goalInvitationService.accept(invitationId);
    }

    @PutMapping("/invitations/{invitationId}/rejection")
    public void reject(@PathVariable long invitationId) {
        goalInvitationService.reject(invitationId);
    }

    @GetMapping("/invitations")
    public List<GoalInvitationDto> getByFilters(@ModelAttribute GoalInvitationFilterDto filters) {
        return goalInvitationService.getByFilters(filters);
    }
}