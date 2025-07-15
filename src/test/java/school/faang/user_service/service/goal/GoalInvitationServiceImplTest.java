package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalInvitationCreateDto;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;
import school.faang.user_service.service.filter.goal.GoalInvitationFilterServiceImpl;
import school.faang.user_service.service.filter.goal.GoalInvitationInvitedIdFilter;
import school.faang.user_service.service.filter.goal.GoalInvitationInviterIdFilter;
import school.faang.user_service.service.filter.goal.GoalInvitationStatusFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {
    private GoalInvitationInviterIdFilter inviterIdFilter = new GoalInvitationInviterIdFilter();
    private GoalInvitationInvitedIdFilter invitedIdFilter = new GoalInvitationInvitedIdFilter();
    private GoalInvitationStatusFilter statusFilter = new GoalInvitationStatusFilter();
    private List<Filter<GoalInvitation, GoalInvitationFilterDto>> filters
            = List.of(inviterIdFilter, invitedIdFilter, statusFilter);
    @Mock
    private UserContext userContext;
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Spy
    private GoalInvitationMapper goalInvitationMapper;
    @Spy
    private FilterService<GoalInvitation, GoalInvitationFilterDto> filterService =
            new GoalInvitationFilterServiceImpl(filters);
    @InjectMocks
    private GoalInvitationServiceImpl service;
    private static User inviter;
    private static User invited;

    @BeforeAll
    static void setUp() {
        inviter = new User();
        inviter.setId(1L);
        inviter.setUsername("inviter");

        invited = new User();
        invited.setId(2L);
        invited.setUsername("invited");
    }

    @Test
    void create_success() {
        var users = new ArrayList<User>();
        users.add(inviter);
        var goal = new Goal();
        goal.setId(1L);
        goal.setUsers(users);
        var goalInvitation = new GoalInvitation();
        goalInvitation.setId(1L);
        goalInvitation.setGoal(goal);
        goalInvitation.setInviter(inviter);
        goalInvitation.setInvited(invited);
        goalInvitation.setStatus(RequestStatus.PENDING);

        when(userContext.getUserId()).thenReturn(inviter.getId());
        when(goalRepository.isUserMember(goal.getId(), inviter.getId()))
                .thenReturn(true);
        when(goalRepository.isUserMember(goal.getId(), invited.getId()))
                .thenReturn(false);
        when(userRepository.getByIdOrThrow(inviter.getId()))
                .thenReturn(inviter);
        when(goalRepository.getByIdOrThrow(goal.getId())).thenReturn(goal);
        when(userRepository.getByIdOrThrow(invited.getId()))
                .thenReturn(invited);
        when(goalInvitationRepository.save(any(GoalInvitation.class)))
                .thenReturn(goalInvitation);

        var createDto = new GoalInvitationCreateDto(invited.getId());
        var actual = service.create(goal.getId(), createDto);
        var expected = goalInvitationMapper.toViewDto(goalInvitation);
        assertEquals(expected, actual);
    }

    static Stream<Arguments> getGoalInvitation() {
        var users = new ArrayList<User>();
        users.add(inviter);
        var goal = new Goal();
        goal.setId(1L);
        goal.setUsers(users);

        var invitation = new GoalInvitation();
        invitation.setId(1L);
        invitation.setGoal(goal);
        invitation.setInviter(inviter);
        invitation.setInvited(invited);
        invitation.setStatus(RequestStatus.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setUpdatedAt(LocalDateTime.now());

        var updatedUser = new ArrayList<User>();
        updatedUser.add(inviter);
        var updatedGoal = new Goal();
        updatedGoal.setId(1L);
        updatedGoal.setUsers(updatedUser);

        var updatedInvitation = new GoalInvitation();
        updatedInvitation.setId(1L);
        updatedInvitation.setGoal(updatedGoal);
        updatedInvitation.setInviter(inviter);
        updatedInvitation.setInvited(invited);
        updatedInvitation.setCreatedAt(LocalDateTime.now());
        updatedInvitation.setUpdatedAt(LocalDateTime.now());
        return Stream.of(Arguments.of(invitation, updatedInvitation));
    }

    @ParameterizedTest
    @MethodSource("getGoalInvitation")
    void accept_success(GoalInvitation invitation, GoalInvitation updatedInvitation) {
        updatedInvitation.setStatus(RequestStatus.ACCEPTED);
        updatedInvitation.getGoal().getUsers().add(invited);
        when(userContext.getUserId()).thenReturn(invited.getId());
        when(goalInvitationRepository.getByIdOrThrow(invitation.getId()))
                .thenReturn(invitation);
        when(goalRepository.isUserMember(invitation.getGoal().getId(), invited.getId()))
                .thenReturn(false);
        when(userRepository.getByIdOrThrow(invitation.getInvited().getId()))
                .thenReturn(invited);

        service.accept(invitation.getId());
        verify(goalRepository).save(eq(updatedInvitation.getGoal()));
        verify(goalInvitationRepository).save(eq(updatedInvitation));
    }

    @ParameterizedTest
    @MethodSource("getGoalInvitation")
    void reject_success(GoalInvitation invitation, GoalInvitation updatedInvitation) {
        updatedInvitation.setStatus(RequestStatus.REJECTED);
        when(userContext.getUserId()).thenReturn(invited.getId());
        when(goalInvitationRepository.getByIdOrThrow(invitation.getId()))
                .thenReturn(invitation);
        when(goalRepository.isUserMember(invitation.getGoal().getId(), invited.getId()))
                .thenReturn(false);


        service.reject(invitation.getId());
        verify(goalInvitationRepository).save(eq(updatedInvitation));
    }

    static Stream<Arguments> provideGetByFiltersParams() {
        var invitation1 = new GoalInvitation();
        invitation1.setId(1L);
        invitation1.setInviter(inviter);
        invitation1.setInvited(invited);
        invitation1.setStatus(RequestStatus.PENDING);
        var invitation2 = new GoalInvitation();
        invitation2.setId(2L);
        invitation2.setInviter(inviter);
        invitation2.setInvited(invited);
        invitation2.setStatus(RequestStatus.ACCEPTED);
        var invitation3 = new GoalInvitation();
        invitation3.setId(3L);
        invitation3.setInviter(invited);
        invitation3.setInvited(inviter);
        invitation3.setStatus(RequestStatus.PENDING);
        List<GoalInvitation> invitations = List.of(invitation1, invitation2, invitation3);
        var filter = new GoalInvitationFilterDto(inviter.getId(), null, RequestStatus.PENDING);
        return Stream.of(Arguments.of(filter, invitations));
    }

    @ParameterizedTest
    @MethodSource("provideGetByFiltersParams")
    void getByFilters_success(GoalInvitationFilterDto filter, List<GoalInvitation> invitations) {
        var expected = filterService.getFilteredList(invitations, filter)
                .stream()
                .map(goalInvitationMapper::toViewDto)
                .toList();

        when(goalInvitationRepository.findAll())
                .thenReturn(invitations);
        var actual = service.getByFilters(filter);
        assertEquals(expected, actual);
    }
}