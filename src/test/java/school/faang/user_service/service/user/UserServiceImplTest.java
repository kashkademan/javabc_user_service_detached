package school.faang.user_service.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.GetUsersDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.user.UserExperienceFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNamePatternFilter;
import school.faang.user_service.filter.user.UserPhonePatternFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private final UserFiltersDto userFiltersDto
            = new UserFiltersDto("Anton", "89991231213", 3, 7);
    private final UserFilter userExperienceFilter = new UserExperienceFilter();
    private final UserFilter userNamePatternFilter = new UserNamePatternFilter();
    private final UserFilter userPhonePatternFilter = new UserPhonePatternFilter();

    private final User firstUser = User.builder()
            .id(22L)
            .username("antony")
            .build();
    private final User secondUser = User.builder()
            .id(23L)
            .username("bobik")
            .build();

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final Event plannedEvent = Event.builder().status(EventStatus.PLANNED).build();
    private final Event inProgressEvent = Event.builder().status(EventStatus.IN_PROGRESS).build();
    private final Event completedEvent = Event.builder().status(EventStatus.COMPLETED).build();
    private final Event participatedEvent = Event.builder().attendees(new ArrayList<>(List.of(firstUser))).build();

    private final Goal goal = Goal.builder()
            .id(4345L)
            .users(new ArrayList<>(List.of(firstUser, secondUser)))
            .build();
    private final Goal setGoal = Goal.builder()
            .id(6632L)
            .users(new ArrayList<>(List.of(firstUser, secondUser)))
            .build();
    private final Goal menteegoal = Goal.builder().mentor(firstUser).build();
    private final Goal menteeSetGoal = Goal.builder().mentor(firstUser).build();

    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Captor
    private ArgumentCaptor<List<Goal>> saveGoalCaptor;
    @Captor
    private ArgumentCaptor<List<Goal>> deleteGoalCaptor;
    @Captor
    private ArgumentCaptor<List<Event>> saveEventCaptor;
    @Captor
    private ArgumentCaptor<List<Event>> deleteEventCaptor;

    @Mock
    private UserRepository userRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private List<UserFilter> userFilters;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MentorshipService mentorshipService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, countryRepository, userMapper, userContext,
                List.of(userExperienceFilter, userNamePatternFilter, userPhonePatternFilter), goalRepository,
                eventRepository, mentorshipService);
    }

    @Test
    void testGetUserThrowsEntityNotFound() {
        when(userRepository.getByIdOrThrow(anyLong())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void testGetUser() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        UserDto actualUser = userService.getById(firstUser.getId());

        Assertions.assertNotNull(actualUser);
        Assertions.assertEquals(firstUser.getId(), actualUser.id());
        Assertions.assertEquals(firstUser.getUsername(), actualUser.username());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfEmptyArgument() {
        Assertions.assertTrue(userService.getUsersByIds(null).isEmpty());
    }

    @Test
    void testGetUsersByIdsReturnEmptyListIfUsersNotFound() {
        Assertions.assertTrue(userService.getUsersByIds(GetUsersDto.builder()
                        .ids(new ArrayList<>(List.of(1L, 2L)))
                        .build())
                .isEmpty());
    }

    @Test
    void testGetUsersByIds() {
        final GetUsersDto getUsersDto = GetUsersDto.builder()
                .ids(new ArrayList<>(List.of(firstUser.getId(), secondUser.getId())))
                .build();

        when(userRepository.findAllById(getUsersDto.ids())).thenReturn(List.of(firstUser, secondUser));

        List<UserDto> actualUsers = userService.getUsersByIds(getUsersDto);
        List<UserDto> expectedUsers = new ArrayList<>(List.of(firstUser, secondUser)).stream()
                .map(userMapper::toUserDto)
                .toList();

        Assertions.assertNotNull(actualUsers);
        Assertions.assertFalse(actualUsers.isEmpty());
        Assertions.assertTrue(actualUsers.containsAll(expectedUsers));
    }


    @Test
    void testGetPremiumUsersPositive() {
        User correctUserWithMinExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMin())
                .build();

        User correctUserWithMaxExp = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongNameUser = User.builder()
                .username("Nikolay")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone("111111111")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(8)
                .build();

        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(correctUserWithMinExp,
                correctUserWithMaxExp, wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> expectedPremiumUsers = Arrays.asList(correctUserWithMinExp, correctUserWithMaxExp)
                .stream()
                .map(userMapper::toUserDto)
                .toList();
        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(2, actualPremiumUsers.size());
        Assertions.assertTrue(actualPremiumUsers.containsAll(expectedPremiumUsers));
    }

    @Test
    void testGetPremiumUsersContainsCorrectWords() {
        User wrongNameUser = User.builder()
                .username(userFiltersDto.namePattern() + "k")
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongPhoneUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern() + "2")
                .experience(userFiltersDto.experienceMax())
                .build();

        User wrongExpUser = User.builder()
                .username(userFiltersDto.namePattern())
                .phone(userFiltersDto.phonePattern())
                .experience(userFiltersDto.experienceMax() + 1)
                .build();

        Mockito.when(userRepository.findPremiumUsers())
                .thenReturn(Stream.of(wrongNameUser, wrongPhoneUser, wrongExpUser));

        List<UserDto> actualPremiumUsers = userService.getPremiumUsers(userFiltersDto);

        Assertions.assertEquals(0, actualPremiumUsers.size());
    }

    @Test
    void testDeactivateUserThrowsExceptionIfUserNotFound() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.deactivateUser(firstUser.getId()));
    }

    @Test
    void testDeactivateUserThrowsExceptionIfUserAlreadyDeactivated() {
        firstUser.setActive(false);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.deactivateUser(firstUser.getId()));
        Assertions.assertEquals("User %d already deactivated".formatted(firstUser.getId()),
                illegalArgumentException.getMessage());
    }

    @Test
    void testDeactivateUserPositiveWithDeleteGoals() {
        goal.setUsers(new ArrayList<>(List.of(firstUser)));
        setGoal.setUsers(new ArrayList<>(List.of(firstUser)));

        deactivateTestSteps(2);
    }

    @Test
    void testDeactivateUserPositiveWithDeleteUserFromGoal() {
        deactivateTestSteps(0);

        verify(goalRepository).deleteUserFromGoal(firstUser.getId(), goal.getId());
        verify(goalRepository).deleteUserFromGoal(firstUser.getId(), setGoal.getId());

    }

    @Test
    void testActivateUserThrowsExceptionIfUserNotFound() {
        when(userRepository.getByIdOrThrow(firstUser.getId())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.activateUser(firstUser.getId()));
    }

    @Test
    void testActivateUserThrowsExceptionIfUserAlreadyDeactivated() {
        firstUser.setActive(true);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.activateUser(firstUser.getId()));
        Assertions.assertEquals("User %d already activated".formatted(firstUser.getId()),
                illegalArgumentException.getMessage());
    }

    @Test
    void testActivateUserPositive() {
        firstUser.setActive(false);

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        userService.activateUser(firstUser.getId());

        verify(userRepository).save(userCaptor.capture());

        Assertions.assertTrue(userCaptor.getValue().isActive());
    }

    private void deactivateTestSteps(int deleteGoalsSize) {
        firstUser.setActive(true);
        firstUser.setGoals(new ArrayList<>(List.of(goal)));
        firstUser.setSetGoals(new ArrayList<>(List.of(setGoal)));
        firstUser.setOwnedEvents(new ArrayList<>(List.of(plannedEvent, inProgressEvent, completedEvent)));
        firstUser.setParticipatedEvents(new ArrayList<>(List.of(participatedEvent)));
        firstUser.setMentees(new ArrayList<>(List.of(secondUser)));

        secondUser.setGoals(new ArrayList<>(List.of(menteegoal)));
        secondUser.setSetGoals(new ArrayList<>(List.of(menteeSetGoal)));

        when(userRepository.getByIdOrThrow(firstUser.getId())).thenReturn(firstUser);

        userService.deactivateUser(firstUser.getId());

        verify(userRepository).save(userCaptor.capture());
        verify(goalRepository).saveAll(saveGoalCaptor.capture());
        verify(goalRepository).deleteAll(deleteGoalCaptor.capture());
        verify(eventRepository).saveAll(saveEventCaptor.capture());
        verify(eventRepository).deleteAll(deleteEventCaptor.capture());
        verify(mentorshipService).deleteMentorship(secondUser.getId(), firstUser.getId());

        User deactivatedUser = userCaptor.getValue();
        int canceledEventsSize = deactivatedUser.getOwnedEvents().stream().filter(event ->
                event.getStatus().equals(EventStatus.CANCELED)).toList().size();

        Assertions.assertFalse(deactivatedUser.isActive());
        Assertions.assertEquals(1, deactivatedUser.getParticipatedEvents().size());
        Assertions.assertEquals(firstUser.getId(), deactivatedUser.getId());
        Assertions.assertEquals(2, canceledEventsSize);
        Assertions.assertEquals(deleteGoalsSize, deleteGoalCaptor.getValue().size());
        Assertions.assertEquals(2, saveGoalCaptor.getValue().size());
        Assertions.assertEquals(3, saveEventCaptor.getValue().size());
        Assertions.assertEquals(2, deleteEventCaptor.getValue().size());

        List<Long> participatedEventAttendees = deactivatedUser.getParticipatedEvents().get(0).getAttendees().stream()
                .map(User::getId).toList();
        if (!participatedEventAttendees.isEmpty()) {
            Assertions.assertFalse(participatedEventAttendees.contains(firstUser.getId()));
        }
    }
}