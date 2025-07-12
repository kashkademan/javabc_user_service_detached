package school.faang.user_service.controller.goal;

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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping("/{goalId}/invitations")
    public GoalInvitationDto create(@PathVariable long goalId, @RequestBody CreateGoalInvitationDto invitationDto) {
        if (invitationDto.getInvitedUserId() == null) {
            throw new DataValidationException("invitedUserId must not be null");
        }
        return goalInvitationService.create(goalId, invitationDto);
    }

    @PutMapping("/invitations/{invitationId}/accept")
    public void accept(@PathVariable long invitationId) {
        goalInvitationService.accept(invitationId);
    }

    @PutMapping("/invitations/{invitationId}/reject")
    public void reject(@PathVariable long invitationId) {
        goalInvitationService.reject(invitationId);
    }

    @GetMapping("/invitations")
    public List<GoalInvitationDto> getByFilters(@ModelAttribute GoalInvitationFilterDto filters) {
        return goalInvitationService.getByFilters(filters);
    }
}