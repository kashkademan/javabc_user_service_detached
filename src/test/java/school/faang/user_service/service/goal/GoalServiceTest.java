package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.goal.filters.GoalDescriptionFilterForTest;
import school.faang.user_service.service.goal.filters.GoalMentorFilterForTest;
import school.faang.user_service.service.goal.filters.GoalStatusFilterForTest;
import school.faang.user_service.service.goal.filters.GoalTitleFilterForTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    private final long currentUserId = 7L;
    private User currentUser;
    private final String correctFilterTitle = "title";
    private final String correctFilterDesc = "descr";
    private final long correctFilterMentorId = 5L;
    private final GoalStatus correctFilterStatus = GoalStatus.COMPLETED;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private GoalMapperImpl goalMapper;

    private GoalServiceImpl goalService;

    private final GoalFilter goalDescriptionFilter = new GoalDescriptionFilterForTest();
    private final GoalFilter goalMentorFilter = new GoalMentorFilterForTest();
    private final GoalFilter goalStatusFilter = new GoalStatusFilterForTest();
    private final GoalFilter goalTitleFilter = new GoalTitleFilterForTest();

    @BeforeEach
    void setUp() {
        goalService = new GoalServiceImpl(goalRepository, userRepository, goalMapper, userContext,
                List.of(goalDescriptionFilter, goalMentorFilter, goalStatusFilter, goalTitleFilter));

        ReflectionTestUtils.setField(goalService, "maxActiveGoals", 2);
        currentUser = User.builder()
                .id(currentUserId)
                .mentees(new ArrayList<>(List.of(
                        User.builder().id(1L).build())
                ))
                .build();
    }

    @Test
    public void testCreateThrowsExceptionWhenMenteeByIdNotFound() {
        final String exceptionMessage = "User not found";
        CreateGoalDto createGoalDto = CreateGoalDto.builder().build();

        when(userRepository.getByIdOrThrow(anyLong())).thenThrow(new EntityNotFoundException(exceptionMessage));

        EntityNotFoundException entityNotFoundException
                = Assertions.assertThrows(EntityNotFoundException.class, () -> goalService.create(createGoalDto));
        Assertions.assertEquals(exceptionMessage, entityNotFoundException.getMessage());
    }

    @Test
    public void testCreateThrowsExceptionWhenUsersListIsEmpty() {
        CreateGoalDto createGoalDto = CreateGoalDto.builder()
                .userIds(List.of())
                .build();

        setupCustomMocksForCreateMethod();

        DataValidationException dataValidationException
                = Assertions.assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));
        Assertions.assertEquals("Users cant be empty", dataValidationException.getMessage());
    }

    @Test
    public void testCreateForbiddenToCreateNotOwnGoal() {
        currentUser.getMentees().add(User.builder().id(2L).build());

        CreateGoalDto createGoalDto = CreateGoalDto.builder()
                .userIds(List.of(currentUserId, 1L))
                .build();

        setupCustomMocksForCreateMethod();

        ForbiddenException forbiddenException
                = Assertions.assertThrows(ForbiddenException.class, () -> goalService.create(createGoalDto));
        Assertions.assertEquals("Forbidden to create goal for chosen users", forbiddenException.getMessage());
    }

    @Test
    public void testCreateForbiddenToCreateMoreThanMaxGoals() {
        CreateGoalDto createGoalDto = CreateGoalDto.builder()
                .userIds(List.of(currentUserId, 1L))
                .build();

        setupCustomMocksForCreateMethod();
        when(goalRepository.findGoalsByUserId(currentUserId)).thenReturn(Stream.of(
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build(),
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build()
        ));

        ForbiddenException forbiddenException
                = Assertions.assertThrows(ForbiddenException.class, () -> goalService.create(createGoalDto));
        Assertions.assertEquals("Forbidden to create goal. User already has %d active goals. Max active goals = %d"
                .formatted(2, 2), forbiddenException.getMessage());
    }

    @Test
    public void testCreateThrowsExceptionWhenMentorByIdNotFound() {
        final String exceptionMessage = "Mentor not found";

        CreateGoalDto createGoalDto = CreateGoalDto.builder()
                .mentorId(3L)
                .userIds(List.of(currentUserId, 1L))
                .build();

        setupCustomMocksForCreateMethod();
        when(goalRepository.findGoalsByUserId(currentUserId)).thenReturn(Stream.of(
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build()
        ));
        when(userRepository.getByIdOrThrow(createGoalDto.mentorId()))
                .thenThrow(new EntityNotFoundException(exceptionMessage));

        EntityNotFoundException entityNotFoundException
                = Assertions.assertThrows(EntityNotFoundException.class, () -> goalService.create(createGoalDto));
        Assertions.assertEquals(exceptionMessage, entityNotFoundException.getMessage());
    }

    @Test
    public void testCreateCreateGoalPositive() {
        CreateGoalDto createGoalDto = CreateGoalDto.builder()
                .mentorId(3L)
                .userIds(List.of(currentUserId, 1L))
                .parentId(8L)
                .deadline(LocalDateTime.now())
                .title("some title")
                .description("some description")
                .build();

        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setId(10L);
        goal.setUsers(List.of(
                User.builder().id(1L).build(),
                User.builder().id(currentUserId).build()
        ));
        goal.setMentor(User.builder().id(3L).build());
        goal.setParent(Goal.builder().id(8L).build());

        setupCustomMocksForCreateMethod();
        when(goalRepository.findGoalsByUserId(currentUserId)).thenReturn(Stream.of(
                Goal.builder()
                        .status(GoalStatus.ACTIVE)
                        .build()
        ));
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        GoalDto actualGoalDto = goalService.create(createGoalDto);

        Assertions.assertEquals(createGoalDto.mentorId(), actualGoalDto.mentorId());
        Assertions.assertEquals(createGoalDto.title(), actualGoalDto.title());
        Assertions.assertEquals(createGoalDto.userIds().stream().sorted().toList(),
                actualGoalDto.userIds().stream().sorted().toList());
    }

    @Test
    public void testDeleteThrowsExceptionWhenGoalByIdNotFound() {
        long goalToDeleteId = 1L;
        final String exceptionMessage = "Goal not found";

        when(goalRepository.getByIdOrThrow(goalToDeleteId)).thenThrow(new EntityNotFoundException(exceptionMessage));

        EntityNotFoundException entityNotFoundException
                = Assertions.assertThrows(EntityNotFoundException.class, () -> goalService.delete(goalToDeleteId));
        Assertions.assertEquals(exceptionMessage, entityNotFoundException.getMessage());
    }

    @Test
    public void testDeletePositiveMentorEqualsCurrentUser() {
        long goalToDeleteId = 1L;
        setupCustomMocksForDeleteMethod(goalToDeleteId, currentUserId, List.of(User.builder().id(2L).build()));

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);

        goalService.delete(goalToDeleteId);

        verify(goalRepository, times(1)).deleteById(argumentCaptor.capture());

        Assertions.assertEquals(goalToDeleteId, argumentCaptor.getValue());
    }

    @Test
    public void testDeletePositiveGoalUsersContainsCurrentUser() {
        long goalToDeleteId = 1L;
        setupCustomMocksForDeleteMethod(goalToDeleteId, 3L, List.of(currentUser));

        ArgumentCaptor<Long> argumentCaptorForGoalId = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> argumentCaptorForUserId = ArgumentCaptor.forClass(Long.class);

        goalService.delete(goalToDeleteId);

        verify(goalRepository, times(1)).deleteUserFromGoal(argumentCaptorForUserId.capture(),
                argumentCaptorForGoalId.capture());

        Assertions.assertEquals(goalToDeleteId, argumentCaptorForGoalId.getValue());
        Assertions.assertEquals(currentUserId, argumentCaptorForUserId.getValue());
    }

    @Test
    public void testDeleteNegativeForbittenToDeleteGoal() {
        long goalToDeleteId = 1L;
        setupCustomMocksForDeleteMethod(goalToDeleteId, 3L,
                List.of(User.builder().id(111L).build()));

        ForbiddenException forbiddenException
                = Assertions.assertThrows(ForbiddenException.class, () -> goalService.delete(goalToDeleteId));
        Assertions.assertEquals("Current user cant delete chosen goal", forbiddenException.getMessage());
    }

    @Test
    public void testUpdateThrowsExceptionWhenGoalByIdNotFound() {
        long goalToUpdateId = 1L;
        final String exceptionMessage = "Goal not found";

        when(goalRepository.getByIdOrThrow(goalToUpdateId)).thenThrow(new EntityNotFoundException(exceptionMessage));

        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class,
                () -> goalService.update(goalToUpdateId, UpdateGoalDto.builder().build()));
        Assertions.assertEquals(exceptionMessage, entityNotFoundException.getMessage());
    }

    @Test
    public void testUpdateThrowsExceptionWhenUpdateGoalInCompletedStatus() {
        long goalToUpdateId = 1L;
        final String exceptionMessage = "Cant update goal in completed status";

        setupCustomMocksForUpdateMethod(goalToUpdateId, 345L,
                List.of(User.builder().build()), GoalStatus.COMPLETED);

        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> goalService.update(goalToUpdateId, UpdateGoalDto.builder().build()));
        Assertions.assertEquals(exceptionMessage, forbiddenException.getMessage());
    }

    @Test
    public void testUpdateThrowsExceptionWhenUpdateNotOwnGoal() {
        long goalToUpdateId = 1L;
        final String exceptionMessage = "Current user cant update chosen goal";

        setupCustomMocksForUpdateMethod(goalToUpdateId, 345L,
                List.of(User.builder().id(124L).build()), GoalStatus.ACTIVE);
        when(userContext.getUserId()).thenReturn(currentUserId);

        ForbiddenException forbiddenException = Assertions.assertThrows(ForbiddenException.class,
                () -> goalService.update(goalToUpdateId, UpdateGoalDto.builder().build()));
        Assertions.assertEquals(exceptionMessage, forbiddenException.getMessage());
    }

    @Test
    public void testUpdatePositive() {
        long goalToUpdateId = 1L;
        UpdateGoalDto updateGoalDto = UpdateGoalDto.builder()
                .mentorId(345L)
                .build();

        Goal goal = setupCustomMocksForUpdateMethod(goalToUpdateId, 63L,
                List.of(currentUser), GoalStatus.ACTIVE);

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(userRepository.getByIdOrThrow(updateGoalDto.mentorId()))
                .thenReturn(User.builder().id(updateGoalDto.mentorId()).build());
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);

        GoalDto updatedGoal = goalService.update(goalToUpdateId, updateGoalDto);
        GoalDto expectedGoalDto = goalMapper.toGoalDto(goal);

        Assertions.assertEquals(expectedGoalDto.mentorId(), updatedGoal.mentorId());
        Assertions.assertEquals(expectedGoalDto.status(), updatedGoal.status());
    }

    @Test
    public void testFiltersPositive() {
        Goal correctGoal = getGoalForFilter(correctFilterTitle, correctFilterDesc,
                correctFilterStatus, correctFilterMentorId);
        Goal wrongTitleGoal = getGoalForFilter("adfg", correctFilterDesc,
                correctFilterStatus, correctFilterMentorId);
        Goal wrongDescGoal = getGoalForFilter(correctFilterTitle, "adsfgh",
                correctFilterStatus, correctFilterMentorId);
        Goal wrongStatusGoal = getGoalForFilter(correctFilterTitle, correctFilterDesc,
                GoalStatus.ACTIVE, correctFilterMentorId);
        Goal wrongMentorGoal = getGoalForFilter(correctFilterTitle, correctFilterDesc,
                correctFilterStatus, 22L);

        when(goalRepository.findAll())
                .thenReturn(List.of(correctGoal, wrongTitleGoal, wrongDescGoal, wrongStatusGoal, wrongMentorGoal));

        List<GoalDto> goalsByFilters
                = goalService.getByFilters(new GoalFilterDto(null, null, null, null));

        GoalDto goalDto = goalsByFilters.get(0);

        Assertions.assertEquals(1, goalsByFilters.size());
        Assertions.assertEquals(correctFilterTitle, goalDto.title());
        Assertions.assertEquals(correctFilterDesc, goalDto.description());
        Assertions.assertEquals(correctFilterMentorId, goalDto.mentorId());
        Assertions.assertEquals(correctFilterStatus, goalDto.status());
    }

    @Test
    public void testFiltersContainsCorrectWords() {
        Goal wrongTitleGoal = getGoalForFilter(correctFilterTitle + 2, correctFilterDesc,
                correctFilterStatus, correctFilterMentorId);
        Goal wrongDescGoal = getGoalForFilter(correctFilterTitle, correctFilterDesc + 2,
                correctFilterStatus, correctFilterMentorId);
        Goal wrongMentorGoal = getGoalForFilter(correctFilterTitle, correctFilterDesc,
                correctFilterStatus, correctFilterMentorId + 50);

        when(goalRepository.findAll())
                .thenReturn(List.of(wrongTitleGoal, wrongDescGoal, wrongMentorGoal));

        List<GoalDto> goalsByFilters
                = goalService.getByFilters(new GoalFilterDto(null, null, null, null));

        Assertions.assertEquals(0, goalsByFilters.size());
    }

    private Goal getGoalForFilter(String title, String description, GoalStatus status, long metorId) {
        return Goal.builder()
                .title(title)
                .description(description)
                .status(status)
                .mentor(User.builder().id(metorId).build())
                .users(Arrays.asList(User.builder().id(34533L).build()))
                .build();
    }

    private void setupCustomMocksForCreateMethod() {
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(userRepository.getByIdOrThrow(currentUserId)).thenReturn(currentUser);
    }

    private Goal setupCustomMocksForDeleteMethod(long goalToDeleteId, long mentorId, List<User> users) {
        Goal goal = Goal.builder()
                .mentor(User.builder().id(mentorId).build())
                .users(users)
                .build();

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(goalRepository.getByIdOrThrow(goalToDeleteId)).thenReturn(goal);

        return goal;
    }

    private Goal setupCustomMocksForUpdateMethod(long goalToDeleteId, long mentorId,
                                                 List<User> users, GoalStatus goalStatus) {
        Goal goal = Goal.builder()
                .mentor(User.builder().id(mentorId).build())
                .users(users)
                .status(goalStatus)
                .build();

        when(goalRepository.getByIdOrThrow(goalToDeleteId)).thenReturn(goal);

        return goal;
    }
}