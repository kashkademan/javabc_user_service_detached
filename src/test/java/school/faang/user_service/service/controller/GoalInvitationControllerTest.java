package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.goal.GoalInvitationService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationControllerTest {

    @Mock
    private GoalInvitationService goalInvitationService;

    @InjectMocks
    private GoalInvitationController controller;

    @Test
    void testCreateInvitation() {
        GoalInvitationDto dto = new GoalInvitationDto();
        controller.createInvitation(dto);
        verify(goalInvitationService).createInvitation(dto);
    }

    @Test
    void testAcceptGoalInvitation() {
        controller.acceptGoalInvitation(10L);
        verify(goalInvitationService).acceptGoalInvitation(10L);
    }

    @Test
    void testRejectGoalInvitation() {
        controller.rejectGoalInvitation(5L);
        verify(goalInvitationService).rejectGoalInvitation(5L);
    }

    @Test
    void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto();
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(100L);

        when(goalInvitationService.getInvitations(filter)).thenReturn(List.of(dto));

        List<GoalInvitationDto> result = controller.getInvitations(filter);

        verify(goalInvitationService).getInvitations(filter);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getGoalId());
    }
}
