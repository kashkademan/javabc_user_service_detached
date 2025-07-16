package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.property.GoalProperty;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ActiveGoalsLimitExceededException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.GoalCompletedException;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class GoalServiceImplTest {
    private GoalServiceImpl goalService;
    private GoalProperty goalProperty;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Spy
    private GoalMapperImpl mapper;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;

    private static final String TITLE = "Test title";
    private static final String DESCRIPTION = "Test description";
    private static final LocalDateTime DEADLINE = LocalDateTime.now().plusMonths(3);
    private static final LocalDateTime NEW_DEADLINE = LocalDateTime.now().plusMonths(4);

    private static final Long MENTOR_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long OTHER_USER_ID = 3L;
    private static final Long PARENT_GOAL_ID = 1L;
    private static final long GOAL_ID = 2L;


    @BeforeEach
    void setUp() {
        goalProperty = new GoalProperty(1, 3);
        goalService = new GoalServiceImpl(goalProperty, goalRepository, userRepository, mapper, userContext);
    }

    @Test
    @DisplayName("Успешное создание цели ментром")
    void positive_whenMentor_shouldCreateGoal() {
        CreateGoalDto createGoalDto = prepareCreateGoalDto(MENTOR_ID, List.of(USER_ID, OTHER_USER_ID));
        GoalDto expected = prepareCreatedExpectedDto(createGoalDto);
        preparePositiveCreateBehavior(createGoalDto, MENTOR_ID);

        GoalDto actual = goalService.create(createGoalDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(userRepository, times(1)).getByIdOrThrow(createGoalDto.mentorId());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное создание цели самим пользователем")
    void positive_whenRegularUser_shouldCreateGoal() {
        CreateGoalDto createGoalDto = prepareCreateGoalDto(null, List.of(USER_ID));
        GoalDto expected = prepareCreatedExpectedDto(createGoalDto);
        preparePositiveCreateBehavior(createGoalDto, USER_ID);

        GoalDto actual = goalService.create(createGoalDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(userRepository, never()).getByIdOrThrow(anyLong());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное обновление цели ментром")
    void positive_whenMentor_shouldUpdateGoal() {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(OTHER_USER_ID, DEADLINE, GoalStatus.COMPLETED);
        GoalDto expected = prepareUpdatedExpectedDto(updateGoalDto, List.of(USER_ID), GoalStatus.COMPLETED);
        preparePositiveUpdateBehavior(updateGoalDto, MENTOR_ID, MENTOR_ID, List.of(USER_ID));

        GoalDto actual = goalService.update(GOAL_ID, updateGoalDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(userRepository, times(1)).getByIdOrThrow(updateGoalDto.mentorId());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное обновление цели самим пользователем")
    void positive_whenRegularUser_shouldUpdateGoal() {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(null, DEADLINE, GoalStatus.COMPLETED);
        GoalDto expected = prepareUpdatedExpectedDto(updateGoalDto, List.of(USER_ID), GoalStatus.COMPLETED);

        preparePositiveUpdateBehavior(updateGoalDto, USER_ID, null, List.of(USER_ID));

        GoalDto actual = goalService.update(GOAL_ID, updateGoalDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(userRepository, never()).getByIdOrThrow(anyLong());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Успешное обновление цели участником")
    void positive_whenParticipant_shouldUpdateGoal() {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(MENTOR_ID, NEW_DEADLINE, GoalStatus.ACTIVE);
        GoalDto expected = prepareUpdatedExpectedDto(updateGoalDto, List.of(USER_ID), GoalStatus.ACTIVE);
        preparePositiveUpdateBehavior(updateGoalDto, USER_ID, MENTOR_ID, List.of(USER_ID));

        GoalDto actual = goalService.update(GOAL_ID, updateGoalDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @EnumSource(GoalStatus.class)
    @DisplayName("Успешное удаление цели ментром")
    void positive_whenMentor_shouldDeleteGoal(GoalStatus status) {
        Goal existsGoal = prepareExistsGoal(MENTOR_ID, List.of(USER_ID), status);
        when(userContext.getUserId()).thenReturn(MENTOR_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID)).thenReturn(existsGoal);

        goalService.delete(GOAL_ID);

        verify(goalRepository, times(1)).deleteById(GOAL_ID);
        verify(goalRepository, never()).deleteUserFromGoal(anyLong(), anyLong());
    }

    @ParameterizedTest
    @EnumSource(GoalStatus.class)
    @DisplayName("Успешное удаление цели пользователем у себя")
    void positive_whenRegularUser_shouldDeleteGoal(GoalStatus status) {
        Goal existsGoal = prepareExistsGoal(null, List.of(USER_ID), status);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID)).thenReturn(existsGoal);

        goalService.delete(GOAL_ID);

        verify(goalRepository, times(1)).deleteById(GOAL_ID);
        verify(goalRepository, never()).deleteUserFromGoal(anyLong(), anyLong());
    }

    @ParameterizedTest
    @EnumSource(GoalStatus.class)
    @DisplayName("Успешное удаление себя из цели одним из участников")
    void positive_whenNotLastParticipant_shouldDeleteYourselfFromGoal(GoalStatus status) {
        Goal existsGoal = prepareExistsGoal(MENTOR_ID, List.of(USER_ID, OTHER_USER_ID), status);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID)).thenReturn(existsGoal);

        goalService.delete(GOAL_ID);

        verify(goalRepository, never()).deleteById(anyLong());
        verify(goalRepository, times(1)).deleteUserFromGoal(USER_ID, GOAL_ID);
    }

    @ParameterizedTest
    @EnumSource(GoalStatus.class)
    @DisplayName("Успешное удаление цели последним участником")
    void positive_whenLastParticipant_shouldDeleteGoal(GoalStatus status) {
        Goal existsGoal = prepareExistsGoal(MENTOR_ID, List.of(USER_ID), status);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID)).thenReturn(existsGoal);

        goalService.delete(GOAL_ID);

        verify(goalRepository, times(1)).deleteById(GOAL_ID);
        verify(goalRepository, never()).deleteUserFromGoal(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Успешная фильтрация целей")
    void getByFilters() {
        GoalFilterDto goalFilterDto = new GoalFilterDto(TITLE, DESCRIPTION, GoalStatus.ACTIVE, MENTOR_ID);
        List<GoalDto> expected = List.of(new GoalDto(
                GOAL_ID, PARENT_GOAL_ID, TITLE, DESCRIPTION,
                null, DEADLINE, null, MENTOR_ID, List.of(USER_ID), GoalStatus.ACTIVE));

        when(goalRepository.findGoalsByFilters(goalFilterDto.titleContains(),
                                               goalFilterDto.descriptionContains(),
                                               goalFilterDto.mentorId(),
                                               goalFilterDto.status()))
                .thenReturn(List.of(prepareExistsGoal(MENTOR_ID, List.of(USER_ID), GoalStatus.ACTIVE)));

        List<GoalDto> actual = goalService.getByFilters(goalFilterDto);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("provideUsersHaveNoCreateAuthority")
    @DisplayName("Ошибка создания цели - недостаточно прав")
    void negative_whenHasNoAuthority_shouldNotCreateAndThrowException(Long mentorId, Long userId, Long currentId) {
        CreateGoalDto createGoalDto = prepareCreateGoalDto(mentorId, List.of(userId));

        when(userContext.getUserId())
                .thenReturn(currentId);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenReturn(prepareUsersBy(createGoalDto.userIds()));

        verify(goalRepository, never()).save(any(Goal.class));
        assertThrows(ForbiddenException.class,
                     () -> goalService.create(createGoalDto));
    }

    @ParameterizedTest
    @MethodSource("provideUsersExceededActiveGoalLimit")
    @DisplayName("Ошибка создания цели - превышен лимит активных целей")
    void negative_whenExceededActiveGoalLimit_shouldThrowException(Long mentorId, Long userId, Long currentId) {
        CreateGoalDto createGoalDto = prepareCreateGoalDto(mentorId, List.of(userId));

        when(userContext.getUserId())
                .thenReturn(currentId);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenReturn(prepareUsersBy(createGoalDto.userIds()));
        when(goalRepository.findUserIdsOverActiveGoalLimit(
                createGoalDto.userIds(), GoalStatus.ACTIVE.ordinal(), goalProperty.activeGoalsLimit()))
                .thenReturn(createGoalDto.userIds());

        verify(goalRepository, never()).save(any(Goal.class));
        assertThrows(ActiveGoalsLimitExceededException.class,
                     () -> goalService.create(createGoalDto));
    }

    @ParameterizedTest
    @MethodSource("provideUsers")
    @DisplayName("Ошибка обновления - цель уже завершена")
    void negative_whenGoalCompleted_shouldThrowException(Long currentId) {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(MENTOR_ID, DEADLINE, GoalStatus.COMPLETED);
        when(userContext.getUserId())
                .thenReturn(currentId);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID))
                .thenReturn(prepareExistsGoal(MENTOR_ID, List.of(USER_ID), GoalStatus.COMPLETED));

        verify(goalRepository, never()).save(any(Goal.class));
        assertThrows(GoalCompletedException.class,
                     () -> goalService.update(GOAL_ID, updateGoalDto));
    }

    @ParameterizedTest
    @MethodSource("provideUsersHaveNoUpdateAuthority")
    @DisplayName("Ошибка обновления - недостаточно прав")
    void negative_whenHasNoAuthority_shouldNotUpdateAndThrowException(Long mentorId, Long userId, Long currentId) {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(mentorId, DEADLINE, GoalStatus.ACTIVE);
        when(userContext.getUserId())
                .thenReturn(currentId);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID))
                .thenReturn(prepareExistsGoal(mentorId, List.of(userId), GoalStatus.ACTIVE));

        verify(goalRepository, never()).save(any(Goal.class));
        assertThrows(ForbiddenException.class,
                     () -> goalService.update(GOAL_ID, updateGoalDto),
                     "User " + currentId + " doesn't have authorities to update goal " + GOAL_ID);
    }

    @Test
    @DisplayName("Ошибка обновления ментра - новый ментор участник цели")
    void negative_whenNewMentorIsParticipant_shouldThrowException() {
        UpdateGoalDto updateGoalDto = prepareUpdateGoalDto(OTHER_USER_ID, DEADLINE, GoalStatus.ACTIVE);
        Goal existsGoal = prepareExistsGoal(MENTOR_ID, List.of(OTHER_USER_ID), GoalStatus.ACTIVE);
        when(userContext.getUserId())
                .thenReturn(MENTOR_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID))
                .thenReturn(existsGoal);

        verify(goalRepository, never()).save(any(Goal.class));
        verify(userRepository, never()).getByIdOrThrow(updateGoalDto.mentorId());
        assertThrows(ForbiddenException.class,
                     () -> goalService.update(GOAL_ID, updateGoalDto),
                     "User " + updateGoalDto.mentorId() + " is a participant in goal " + GOAL_ID
                     + " and cannot be appointment as a new mentor by user " + MENTOR_ID);
    }

    @Test
    @DisplayName("Ошибка при удалении цели - недостаточно прав")
    void negative_whenHasNoAuthority_shouldThrowException() {
        Goal existsGoal = prepareExistsGoal(MENTOR_ID, List.of(USER_ID), GoalStatus.COMPLETED);
        when(userContext.getUserId())
                .thenReturn(OTHER_USER_ID);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID))
                .thenReturn(existsGoal);

        verify(goalRepository, never()).deleteById(anyLong());
        verify(goalRepository, never()).deleteUserFromGoal(anyLong(), anyLong());
        assertThrows(ForbiddenException.class,
                     () -> goalService.delete(GOAL_ID));
    }

    // --------------------------

    private CreateGoalDto prepareCreateGoalDto(Long mentorId, List<Long> userIds) {
        return new CreateGoalDto(PARENT_GOAL_ID, TITLE, DESCRIPTION, DEADLINE, mentorId, userIds);
    }

    private GoalDto prepareCreatedExpectedDto(CreateGoalDto createGoalDto) {
        return new GoalDto(GOAL_ID,
                           createGoalDto.parentId(),
                           createGoalDto.title(),
                           createGoalDto.description(),
                           null, createGoalDto.deadline(), null,
                           createGoalDto.mentorId(),
                           createGoalDto.userIds(),
                           GoalStatus.ACTIVE);
    }

    private void preparePositiveCreateBehavior(CreateGoalDto createGoalDto, Long currentId) {
        when(userContext.getUserId())
                .thenReturn(currentId);
        when(userRepository.findAllById(createGoalDto.userIds()))
                .thenReturn(prepareUsersBy(createGoalDto.userIds()));
        if (createGoalDto.mentorId() != null) {
            when(userRepository.getByIdOrThrow(createGoalDto.mentorId()))
                    .thenReturn(prepareUserBy(createGoalDto.mentorId()));
        }
        when(goalRepository.getByIdOrThrow(createGoalDto.parentId()))
                .thenReturn(prepareGoal(createGoalDto.parentId()));
        when(goalRepository.save(goalCaptor.capture()))
                .thenAnswer(invocation -> {
                    Goal goal = goalCaptor.getValue();
                    goal.setId(GOAL_ID);
                    return goal;
                });
    }

    private UpdateGoalDto prepareUpdateGoalDto(Long mentorId, LocalDateTime deadline, GoalStatus goalStatus) {
        return new UpdateGoalDto(TITLE, DESCRIPTION, deadline, mentorId, goalStatus);
    }

    private GoalDto prepareUpdatedExpectedDto(
            UpdateGoalDto updateGoalDto, List<Long> userIds, GoalStatus status) {
        return new GoalDto(GOAL_ID,
                           PARENT_GOAL_ID,
                           updateGoalDto.title(),
                           updateGoalDto.description(),
                           null, updateGoalDto.deadline(), null,
                           updateGoalDto.mentorId(),
                           userIds,
                           status);
    }

    private void preparePositiveUpdateBehavior(
            UpdateGoalDto updateGoalDto, Long currentId, Long mentorId, List<Long> userIds) {
        Goal existsGoal = prepareExistsGoal(mentorId, userIds, GoalStatus.ACTIVE);
        when(userContext.getUserId())
                .thenReturn(currentId);
        when(goalRepository.getGoalWithUsersByIdOrThrow(GOAL_ID))
                .thenReturn(existsGoal);
        if (updateGoalDto.mentorId() != null) {
            lenient().when(userRepository.getByIdOrThrow(updateGoalDto.mentorId()))
                    .thenReturn(prepareUserBy(updateGoalDto.mentorId()));
        }
        when(goalRepository.save(goalCaptor.capture()))
                .thenAnswer(invocation -> goalCaptor.getValue());
    }


    private Goal prepareGoal(Long id) {
        return Goal.builder()
                .id(id)
                .build();
    }

    private User prepareUserBy(Long userId) {
        if (userId == null) {
            return null;
        }
        return User.builder()
                .id(userId)
                .build();
    }

    private List<User> prepareUsersBy(List<Long> userIds) {
        return userIds.stream()
                .map(id -> User.builder().id(id).build())
                .toList();
    }

    private Goal prepareExistsGoal(Long mentorId, List<Long> userIds, GoalStatus status) {
        return Goal.builder()
                .id(GOAL_ID)
                .parent(prepareGoal(PARENT_GOAL_ID))
                .title(TITLE)
                .description(DESCRIPTION)
                .deadline(DEADLINE)
                .users(prepareUsersBy(userIds))
                .mentor(prepareUserBy(mentorId))
                .status(status)
                .build();
    }

    private static Stream<Long> provideUsers() {
        return Stream.of(MENTOR_ID, USER_ID);
    }

    private static Stream<Arguments> provideUsersHaveNoCreateAuthority() {
        return Stream.of(
                Arguments.of(MENTOR_ID, USER_ID, OTHER_USER_ID),
                Arguments.of(MENTOR_ID, USER_ID, USER_ID),
                Arguments.of(null, USER_ID, OTHER_USER_ID));
    }

    private static Stream<Arguments> provideUsersExceededActiveGoalLimit() {
        return Stream.of(
                Arguments.of(MENTOR_ID, USER_ID, MENTOR_ID),
                Arguments.of(null, USER_ID, USER_ID));
    }

    private static Stream<Arguments> provideUsersHaveNoUpdateAuthority() {
        return Stream.of(
                Arguments.of(MENTOR_ID, USER_ID, OTHER_USER_ID),
                Arguments.of(null, USER_ID, OTHER_USER_ID));
    }
}