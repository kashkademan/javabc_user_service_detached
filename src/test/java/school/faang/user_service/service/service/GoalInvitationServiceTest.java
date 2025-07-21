package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoalInvitationServiceTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalService goalService;
    @Mock
    private UserService userService;

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;

    @InjectMocks
    private GoalInvitationService goalInvitationService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInvitation_shouldSaveEntityWhenValid() {
        final GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(1L);
        dto.setInviterId(2L);
        dto.setInvitedUserId(3L);

        final Goal goal = new Goal();
        final User inviter = new User();
        final User invited = new User();

        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(goalService.getGoalOrThrow(1L)).thenReturn(goal);
        when(userService.getUserById(2L)).thenReturn(inviter);
        when(userService.getUserById(3L)).thenReturn(invited);

        goalInvitationService.createInvitation(dto);

        final ArgumentCaptor<GoalInvitation> captor =
                ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository).save(captor.capture());

        final GoalInvitation saved = captor.getValue();
        assertEquals(goal, saved.getGoal());
        assertEquals(inviter, saved.getInviter());
        assertEquals(invited, saved.getInvited());
        assertEquals(RequestStatus.PENDING, saved.getStatus());
    }

    @Test
    void createInvitation_shouldThrowWhenInviterOrInvitedIsNull() {
        final GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(1L);
        dto.setInviterId(null);
        dto.setInvitedUserId(3L);

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(dto));
    }

    @Test
    void createInvitation_shouldThrowWhenInviterEqualsInvited() {
        final GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(1L);
        dto.setInviterId(2L);
        dto.setInvitedUserId(2L);

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(dto));
    }

    @Test
    void createInvitation_shouldThrowWhenUserDoesNotExist() {
        final GoalInvitationDto dto = new GoalInvitationDto();
        dto.setGoalId(1L);
        dto.setInviterId(2L);
        dto.setInvitedUserId(3L);

        when(userRepository.existsById(2L)).thenReturn(false);
        when(userRepository.existsById(3L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(dto));
    }

    @Test
    void acceptGoalInvitation_shouldAcceptWhenValid() {
        final User invited = new User();
        invited.setGoals(new ArrayList<>());

        final Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());

        final GoalInvitation invitation = new GoalInvitation();
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        goalInvitationService.acceptGoalInvitation(1L);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
        assertTrue(invited.getGoals().contains(goal));
        assertTrue(goal.getUsers().contains(invited));
        verify(userService).updateUser(invited);
    }

    @Test
    void acceptGoalInvitation_shouldThrowWhenNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));
    }

    @Test
    void acceptGoalInvitation_shouldThrowWhenUserHasMaxGoals() {
        final User invited = new User();
        invited.setGoals(List.of(new Goal(), new Goal(), new Goal()));

        final Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());

        final GoalInvitation invitation = new GoalInvitation();
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));
    }

    @Test
    void acceptGoalInvitation_shouldThrowWhenUserAlreadyInGoal() {
        final User invited = new User();
        invited.setGoals(new ArrayList<>());

        final Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());
        goal.getUsers().add(invited);

        final GoalInvitation invitation = new GoalInvitation();
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));
    }

    @Test
    void rejectGoalInvitation_shouldSetRejectedWhenValid() {
        final Goal goal = new Goal();
        final GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        goalInvitationService.rejectGoalInvitation(1L);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void rejectGoalInvitation_shouldThrowWhenGoalNull() {
        final GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(null);

        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.rejectGoalInvitation(1L));
    }

    @Test
    void rejectGoalInvitation_shouldThrowWhenNotFound() {
        when(goalInvitationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.rejectGoalInvitation(1L));
    }

    @Test
    void getInvitations_shouldFilterByInviterId() {
        final GoalInvitation inv1 = new GoalInvitation();
        final GoalInvitation inv2 = new GoalInvitation();
        final User inviter1 = new User();
        final User inviter2 = new User();
        inviter1.setId(10L);
        inviter2.setId(20L);
        inv1.setInviter(inviter1);
        inv2.setInviter(inviter2);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(inv1, inv2));

        final InvitationFilterDto filter = new InvitationFilterDto();
        filter.setInviterId(10L);

        final List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
    }

    @Test
    void getInvitations_shouldFilterByInvitedId() {
        final GoalInvitation inv1 = new GoalInvitation();
        final GoalInvitation inv2 = new GoalInvitation();
        final User invited1 = new User();
        final User invited2 = new User();
        invited1.setId(1L);
        invited2.setId(2L);
        inv1.setInvited(invited1);
        inv2.setInvited(invited2);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(inv1, inv2));

        final InvitationFilterDto filter = new InvitationFilterDto();
        filter.setInvitedId(2L);

        final List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
    }

    @Test
    void getInvitations_shouldFilterByStatus() {
        final GoalInvitation inv1 = new GoalInvitation();
        final GoalInvitation inv2 = new GoalInvitation();
        inv1.setStatus(RequestStatus.PENDING);
        inv2.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(inv1, inv2));

        final InvitationFilterDto filter = new InvitationFilterDto();
        filter.setStatus(RequestStatus.ACCEPTED);

        final List<GoalInvitationDto> result = goalInvitationService.getInvitations(filter);

        assertEquals(1, result.size());
    }
}
