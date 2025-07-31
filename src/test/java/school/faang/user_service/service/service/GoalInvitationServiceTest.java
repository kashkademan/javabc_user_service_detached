package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal_invitation.GoalInvitationFilter;
import school.faang.user_service.mapper.GoalInvitationMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.goal.GoalInvitationService;
import school.faang.user_service.service.goal.GoalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
    @Mock
    private List<GoalInvitationFilter> filters;  // нужно мокать, чтобы сервис работал
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
        GoalInvitationCreateDto dto = new GoalInvitationCreateDto();
        dto.setGoalId(1L);
        dto.setInviterId(2L);
        dto.setInvitedUserId(3L);

        Goal goal = new Goal();
        User inviter = new User();
        User invited = new User();

        when(userRepository.existsById(2L)).thenReturn(true);
        when(userRepository.existsById(3L)).thenReturn(true);
        when(goalService.getGoalOrThrow(1L)).thenReturn(goal);
        when(userService.getUserById(2L)).thenReturn(inviter);
        when(userService.getUserById(3L)).thenReturn(invited);

        goalInvitationService.createInvitation(dto);

        ArgumentCaptor<GoalInvitation> captor = ArgumentCaptor.forClass(GoalInvitation.class);
        verify(goalInvitationRepository).save(captor.capture());

        GoalInvitation saved = captor.getValue();
        assertEquals(goal, saved.getGoal());
        assertEquals(inviter, saved.getInviter());
        assertEquals(invited, saved.getInvited());
        assertEquals(RequestStatus.PENDING, saved.getStatus());
    }

    @Test
    void createInvitation_shouldThrowWhenInviterOrInvitedIsNull() {
        GoalInvitationCreateDto dto = new GoalInvitationCreateDto();
        dto.setGoalId(1L);
        dto.setInviterId(null);
        dto.setInvitedUserId(3L);

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(dto));
    }

    @Test
    void createInvitation_shouldThrowWhenInviterEqualsInvited() {
        GoalInvitationCreateDto dto = new GoalInvitationCreateDto();
        dto.setGoalId(1L);
        dto.setInviterId(2L);
        dto.setInvitedUserId(2L);

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.createInvitation(dto));
    }

    @Test
    void createInvitation_shouldThrowWhenUserDoesNotExist() {
        GoalInvitationCreateDto dto = new GoalInvitationCreateDto();
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
        User invited = new User();
        invited.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());

        GoalInvitation invitation = new GoalInvitation();
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
        User invited = new User();
        invited.setGoals(List.of(new Goal(), new Goal(), new Goal()));

        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());

        GoalInvitation invitation = new GoalInvitation();
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));
    }

    @Test
    void acceptGoalInvitation_shouldThrowWhenUserAlreadyInGoal() {
        User invited = new User();
        invited.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>());
        goal.getUsers().add(invited);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setInvited(invited);
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        assertThrows(IllegalArgumentException.class, () ->
                goalInvitationService.acceptGoalInvitation(1L));
    }

    @Test
    void rejectGoalInvitation_shouldSetRejectedWhenValid() {
        Goal goal = new Goal();
        GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);

        when(goalInvitationRepository.findById(1L))
                .thenReturn(Optional.of(invitation));

        goalInvitationService.rejectGoalInvitation(1L);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    void rejectGoalInvitation_shouldThrowWhenGoalNull() {
        GoalInvitation invitation = new GoalInvitation();
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
    void getInvitations_shouldApplyFilters() {
        GoalInvitation invitation = new GoalInvitation();
        List<GoalInvitation> invitations = List.of(invitation);

        when(goalInvitationRepository.findAll()).thenReturn(invitations);

        // Мокаем фильтры
        GoalInvitationFilter filter = mock(GoalInvitationFilter.class);
        when(filters.iterator()).thenReturn(List.of(filter).iterator());
        when(filter.isApplicable(any())).thenReturn(true);
        when(filter.apply(any(), any())).thenAnswer(invocation ->
                ((Stream<GoalInvitation>) invocation.getArgument(0)).filter(inv -> true));

        InvitationFilterDto filterDto = new InvitationFilterDto();
        List<GoalInvitationDto> result = goalInvitationService.getInvitations(filterDto);

        assertEquals(1, result.size());
        verify(filter, times(1)).apply(any(), eq(filterDto));
    }
}
