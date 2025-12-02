package school.faang.user_service.service.goal;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.CreateGoalInvitationMapper;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceImplTest {

    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalInvitationMapper goalInvitationMapper = Mappers.getMapper(GoalInvitationMapper.class);
    @Mock
    private UserContext userContext;
    @Mock
    private ValidationService validationService;
    @Spy
    private CreateGoalInvitationMapper createGoalInvitationMapper = Mappers.getMapper(CreateGoalInvitationMapper.class);

    @InjectMocks
    private GoalInvitationServiceImpl service;

    private static final Long GOAL_ID = 2L;
    private static final Long INVITED_USER_ID = 1L;
    private static final Long CURRENT_USER_ID = 10L;
    private static final Long ANOTHER_USER_ID = 99L;
    private static final Long INVITATION_ID = 1L;


    private User invitedUser;
    private User currentUser;
    private Goal goal;
    private GoalInvitation invitation;

    @BeforeEach
    void setUp() {
        invitedUser = new User();
        invitedUser.setId(INVITED_USER_ID);

        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setGoals(new ArrayList<>());

        goal = new Goal();
        goal.setId(GOAL_ID);
        goal.setInvitations(new ArrayList<>());
        goal.setUsers(new ArrayList<>());
        goal.setStatus(GoalStatus.ACTIVE);

        invitation = new GoalInvitation();
        invitation.setId(INVITATION_ID);
        invitation.setGoal(goal);
        invitation.setInvited(invitedUser);
        invitation.setStatus(RequestStatus.PENDING);
    }

    @Test
    public void testSuccessCreation() {
        GoalInvitation savedInvitation = new GoalInvitation();
        savedInvitation.setId(10L);
        savedInvitation.setInvited(invitedUser);
        savedInvitation.setStatus(RequestStatus.PENDING);
        savedInvitation.setGoal(goal);

        CreateGoalInvitationDto invitationDto = new CreateGoalInvitationDto(INVITED_USER_ID, GOAL_ID);

        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(goal);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(savedInvitation);
        when(userRepository.getByIdOrThrow(INVITED_USER_ID)).thenReturn(invitedUser);

        GoalInvitationDto goalInvitationDto = service.create(GOAL_ID, invitationDto);
        GoalInvitationDto expectedDto = new GoalInvitationDto(
                10L,
                new UserDto(1L, "John", "qwer@123.ru", "12212323", "lalal"),
                RequestStatus.PENDING,
                2L);

        assertNotNull(goalInvitationDto);
        assertEquals(expectedDto.status(), goalInvitationDto.status());
        assertEquals(expectedDto.goalId(), goalInvitationDto.goalId());

        verify(goalRepository).getByIdOrThrow(GOAL_ID);
        verify(userRepository).getByIdOrThrow(INVITED_USER_ID);
        verify(goalInvitationRepository).save(any(GoalInvitation.class));
    }

    @Test
    public void testFailedCreation() {
        goal.setUsers(List.of(invitedUser));
        CreateGoalInvitationDto dto = new CreateGoalInvitationDto(INVITED_USER_ID, GOAL_ID);

        when(goalRepository.getByIdOrThrow(GOAL_ID)).thenReturn(goal);
        doThrow(new ForbiddenException("Invited User is already working on this goal!"))
                .when(validationService).validateCreation(goal, dto);

        assertThrows(ForbiddenException.class, () -> service.create(GOAL_ID, dto));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testAcceptSuccess() {
        when(goalInvitationRepository.getByIdOrThrow(INVITATION_ID)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(userRepository.getByIdOrThrow(CURRENT_USER_ID)).thenReturn(currentUser);

        service.accept(INVITATION_ID);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
        assertEquals(1, goal.getUsers().size());
        assertEquals(currentUser, goal.getUsers().get(0));

        verify(goalInvitationRepository).getByIdOrThrow(INVITATION_ID);
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testAcceptForbidden() {
        invitation.getInvited().setId(ANOTHER_USER_ID);

        when(goalInvitationRepository.getByIdOrThrow(INVITATION_ID)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        doThrow(new ForbiddenException("Accept the invitation can invited user only!"))
                .when(validationService).canAcceptOrReject(invitation.getInvited().getId());

        assertThrows(ForbiddenException.class, () -> service.accept(INVITATION_ID));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testRejectSuccess() {
        when(goalInvitationRepository.getByIdOrThrow(INVITATION_ID)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);

        service.reject(INVITATION_ID);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testRejectForbidden() {
        invitation.getInvited().setId(ANOTHER_USER_ID);

        when(goalInvitationRepository.getByIdOrThrow(INVITATION_ID)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        doThrow(new ForbiddenException("Reject the invitation can invited user only!"))
                .when(validationService).canAcceptOrReject(invitation.getInvited().getId());

        assertThrows(ForbiddenException.class, () -> service.reject(INVITATION_ID));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testGetByFiltersSuccess() {
        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInvited(invitedUser);
        invitation2.setGoal(goal);
        invitation2.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(invitation, invitation2));

        var filters = new GoalInvitationFilterDto(INVITED_USER_ID, RequestStatus.PENDING);
        List<GoalInvitationDto> result = service.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).status());
        verify(goalInvitationRepository).findAll();
    }

    @Test
    public void testGetByFiltersNoMatch() {
        invitation.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(invitation));

        var filters = new GoalInvitationFilterDto(INVITED_USER_ID, RequestStatus.ACCEPTED);
        List<GoalInvitationDto> result = service.getByFilters(filters);

        assertEquals(0, result.size());
        verify(goalInvitationRepository).findAll();

    }
}
