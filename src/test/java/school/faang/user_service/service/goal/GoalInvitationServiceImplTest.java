package school.faang.user_service.service.goal;

import java.util.ArrayList;
import java.util.List;
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
    @Spy
    private CreateGoalInvitationMapper createGoalInvitationMapper = Mappers.getMapper(CreateGoalInvitationMapper.class);

    @InjectMocks
    private GoalInvitationServiceImpl service;


    @Test
    public void testSuccessCreation() {
        Long invitedUserId = 1L;

        User invitedUser = new User();
        invitedUser.setId(invitedUserId);

        Goal goal = new Goal();
        goal.setId(2L);
        goal.setInvitations(new ArrayList<>());
        goal.setUsers(new ArrayList<>());
        goal.setStatus(GoalStatus.ACTIVE);

        GoalInvitation savedInvitation = new GoalInvitation();
        savedInvitation.setId(10L);
        savedInvitation.setInvited(invitedUser);
        savedInvitation.setStatus(RequestStatus.PENDING);
        savedInvitation.setGoal(goal);

        Long goalId = 2L;

        CreateGoalInvitationDto invitationDto =
                new CreateGoalInvitationDto(invitedUserId, goalId);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalInvitationRepository.save(any(GoalInvitation.class))).thenReturn(savedInvitation);
        when(userRepository.getByIdOrThrow(invitedUserId)).thenReturn(invitedUser);

        GoalInvitationDto goalInvitationDto = service.create(goalId, invitationDto);
        GoalInvitationDto expectedDto = new GoalInvitationDto(
                10L,
                new UserDto(1L, "John", "qwer@123.ru", "12212323", "lalal"),
                RequestStatus.PENDING,
                2L);

        assertNotNull(goalInvitationDto);
        assertEquals(expectedDto.status(), goalInvitationDto.status());
        assertEquals(expectedDto.goalId(), goalInvitationDto.goalId());

        verify(goalRepository).getByIdOrThrow(goalId);
        verify(userRepository).getByIdOrThrow(invitedUserId);
        verify(goalInvitationRepository).save(any(GoalInvitation.class));
    }

    @Test
    public void testFailedCreation() {
        long goalId = 1L;
        long invitedUserId = 2L;

        User invitedUser = new User();
        invitedUser.setId(invitedUserId);

        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setUsers(List.of(invitedUser));
        goal.setInvitations(new ArrayList<>());

        CreateGoalInvitationDto dto = new CreateGoalInvitationDto(invitedUserId, goalId);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);

        assertThrows(ForbiddenException.class, () -> service.create(goalId, dto));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testAcceptSuccess() {
        long userId = 10L;

        User invitedUser = new User();
        invitedUser.setId(userId);

        User currentUser = new User();
        currentUser.setId(userId);
        currentUser.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setId(2L);
        goal.setUsers(new ArrayList<>());

        long invitationId = 1L;

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(invitationId);
        invitation.setGoal(goal);
        invitation.setInvited(invitedUser);
        invitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.getByIdOrThrow(invitationId)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(userId);
        when(userRepository.getByIdOrThrow(userId)).thenReturn(currentUser);

        service.accept(invitationId);

        assertEquals(RequestStatus.ACCEPTED, invitation.getStatus());
        assertEquals(1, goal.getUsers().size());
        assertEquals(currentUser, goal.getUsers().get(0));

        verify(goalInvitationRepository).getByIdOrThrow(invitationId);
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testAcceptForbidden() {
        long invitationId = 1L;
        long anotherUserId = 99L;

        User invitedUser = new User();
        invitedUser.setId(anotherUserId);

        Goal goal = new Goal();
        goal.setId(2L);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(invitationId);
        invitation.setGoal(goal);
        invitation.setInvited(invitedUser);
        invitation.setStatus(RequestStatus.PENDING);

        long userId = 10L;

        when(goalInvitationRepository.getByIdOrThrow(invitationId)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(ForbiddenException.class, () -> service.accept(invitationId));

        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testRejectSuccess() {
        long invitationId = 1L;
        long userId = 20L;

        User invitedUser = new User();
        invitedUser.setId(userId);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(invitationId);
        invitation.setInvited(invitedUser);
        invitation.setStatus(RequestStatus.PENDING);

        when(goalInvitationRepository.getByIdOrThrow(invitationId)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(userId);

        service.reject(invitationId);

        assertEquals(RequestStatus.REJECTED, invitation.getStatus());
        verify(goalInvitationRepository).save(invitation);
    }

    @Test
    public void testRejectForbidden() {
        long invitationId = 1L;
        long anotherUserId = 99L;

        User invitedUser = new User();
        invitedUser.setId(anotherUserId);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(invitationId);
        invitation.setInvited(invitedUser);
        invitation.setStatus(RequestStatus.PENDING);

        long userId = 10L;

        when(goalInvitationRepository.getByIdOrThrow(invitationId)).thenReturn(invitation);
        when(userContext.getUserId()).thenReturn(userId);

        assertThrows(ForbiddenException.class, () -> service.reject(invitationId));
        verify(goalInvitationRepository, never()).save(any());
    }

    @Test
    public void testGetByFiltersSuccess() {
        long invitedId = 1L;

        Goal goal = new Goal();
        goal.setId(100L);

        User invitedUser = new User();
        invitedUser.setId(invitedId);

        GoalInvitation invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setInvited(invitedUser);
        invitation1.setGoal(goal);
        invitation1.setStatus(RequestStatus.PENDING);

        GoalInvitation invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInvited(invitedUser);
        invitation2.setGoal(goal);
        invitation2.setStatus(RequestStatus.ACCEPTED);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(invitation1, invitation2));

        var filters = new GoalInvitationFilterDto(invitedId, RequestStatus.PENDING);
        List<GoalInvitationDto> result = service.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals(RequestStatus.PENDING, result.get(0).status());
        verify(goalInvitationRepository).findAll();
    }

    @Test
    public void testGetByFiltersNoMatch() {
        long invitedId = 1L;

        Goal goal = new Goal();
        goal.setId(100L);

        User invitedUser = new User();
        invitedUser.setId(invitedId);

        GoalInvitation invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setInvited(invitedUser);
        invitation.setGoal(goal);
        invitation.setStatus(RequestStatus.REJECTED);

        when(goalInvitationRepository.findAll()).thenReturn(List.of(invitation));

        var filters = new GoalInvitationFilterDto(invitedId, RequestStatus.ACCEPTED);
        List<GoalInvitationDto> result = service.getByFilters(filters);

        assertEquals(0, result.size());
        verify(goalInvitationRepository).findAll();
    }
}
