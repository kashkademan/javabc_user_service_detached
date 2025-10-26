package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class GoalInvitationController {

    private final GoalInvitationService service;

    @PostMapping("/{goalId}")
    public ResponseEntity<GoalInvitationDto> create(@PathVariable long goalId,
                                                    @Valid @RequestBody CreateGoalInvitationDto invitationDto) {
        GoalInvitationDto goalInvitationDto = service.create(goalId, invitationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(goalInvitationDto);
    }

    @PostMapping("/{invitationId}/accept")
    public ResponseEntity<Void> accept(@PathVariable long invitationId) {
        service.accept(invitationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{invitationId}/reject")
    public ResponseEntity<Void> reject(@PathVariable long invitationId) {
        service.reject(invitationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<GoalInvitationDto>> getByFilters(@Valid GoalInvitationFilterDto filters) {
        List<GoalInvitationDto> listOfGoalInvitation = service.getByFilters(filters);
        return ResponseEntity.status(HttpStatus.OK).body(listOfGoalInvitation);
    }
}


