package school.faang.user_service.service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.goal.GoalInvitationController;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
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
        GoalInvitationCreateDto createDto = new GoalInvitationCreateDto();
        GoalInvitationDto savedDto = new GoalInvitationDto();
        savedDto.setGoalId(123L);

        when(goalInvitationService.createInvitation(createDto)).thenReturn(savedDto);

        ResponseEntity<GoalInvitationDto> response = controller.createInvitation(createDto);

        verify(goalInvitationService).createInvitation(createDto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(123L, response.getBody().getGoalId());
    }

    @Test
    void testAcceptGoalInvitation() {
        ResponseEntity<Void> response = controller.acceptGoalInvitation(10L);

        verify(goalInvitationService).acceptGoalInvitation(10L);
        assertEquals(204, response.getStatusCodeValue()); // No Content
    }

    @Test
    void testRejectGoalInvitation() {
        ResponseEntity<Void> response = controller.rejectGoalInvitation(5L);

        verify(goalInvitationService).rejectGoalInvitation(5L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGetInvitations() {
        InvitationFilterDto filter = new InvitationFilterDto();
        GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(100L);

        when(goalInvitationService.getInvitations(filter)).thenReturn(List.of(dto));

        ResponseEntity<List<GoalInvitationDto>> response = controller.getInvitations(filter);

        verify(goalInvitationService).getInvitations(filter);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getGoalId());
    }
}
