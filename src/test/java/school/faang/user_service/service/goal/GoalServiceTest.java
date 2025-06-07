package school.faang.user_service.service.goal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalNotExistException;
import school.faang.user_service.exception.goal.UpdateComleteGoalException;
import school.faang.user_service.exception.goal.UserNotGoalOwnerException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;
import school.faang.user_service.validation.skill.SkillValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    private static final String TITLE = "Test goal";
    private static final String PARENT_TITLE = "Parent goal";
    private static final String DESCRIPTION = "Test description";
    private static final long USER_ID = 1L;
    private static final long GOAL_ID = 2L;
    private static final long PARENT_ID = 3L;
    private static final Function<Long, Skill> ID_TO_SKILL = id -> Skill.builder().id(id).build();

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private SkillService skillService;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserService userService;
    @InjectMocks
    private GoalService goalService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(USER_ID).build();
    }

    @Test
    public void testGetGoal() {
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(new Goal()));

        assertDoesNotThrow(() -> goalService.getGoalById(GOAL_ID));
    }

    @Test
    public void testGetNotExistingGoal() {
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.empty());

        assertThrows(GoalNotExistException.class, () -> goalService.getGoalById(GOAL_ID));
    }

    @Test
    public void testCreateGoalNoSkillNoParent() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);
        Goal createGoalData = Goal.builder()
                .title(TITLE)
                .description(DESCRIPTION)
                .deadline(deadline)
                .build();

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userService.getUserById(USER_ID)).thenReturn(user);

        goalService.createGoal(createGoalData, new ArrayList<>(), null);

        verify(goalValidator).validateMaxActiveGoalLimitPerUser(USER_ID);
        verify(skillValidator).validateExistingSkills(new ArrayList<>());
        verify(goalRepository, times(1)).save(captor.capture());

        Goal createdGoal = captor.getValue();

        assertEquals(TITLE, createdGoal.getTitle());
        assertEquals(DESCRIPTION, createdGoal.getDescription());
        assertEquals(deadline, createdGoal.getDeadline());

        assertEquals(GoalStatus.ACTIVE, createdGoal.getStatus());
        assertEquals(new ArrayList<>(), createdGoal.getSkillsToAchieve());
        assertNull(createdGoal.getParent());
        Assertions.assertThat(createdGoal.getUsers())
                .containsExactlyElementsOf(List.of(user));
    }

    @Test
    public void testCreateGoalWithParent() {
        Goal onlyTitleGoalData = Goal.builder()
                .title(TITLE)
                .build();

        Goal parentGoal = Goal.builder()
                .id(PARENT_ID)
                .title(PARENT_TITLE)
                .build();

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(goalRepository.findById(PARENT_ID)).thenReturn(Optional.ofNullable(parentGoal));

        goalService.createGoal(onlyTitleGoalData, new ArrayList<>(), PARENT_ID);

        verify(goalRepository, times(1)).save(captor.capture());

        Goal createdGoal = captor.getValue();
        assertNotNull(createdGoal.getParent());
        assertEquals(PARENT_ID, createdGoal.getParent().getId());
        assertEquals(PARENT_TITLE, createdGoal.getParent().getTitle());
    }

    @Test
    public void testCreateGoalWithSkills() {
        Goal onlyTitleGoalData = Goal.builder()
                .title(TITLE)
                .build();

        List<Long> skillsId = List.of(1L, 2L, 3L);
        List<Skill> skills = List.of(
                Skill.builder().id(1).build(),
                Skill.builder().id(2).build(),
                Skill.builder().id(3).build()
        );

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userService.getUserById(USER_ID)).thenReturn(user);
        when(skillService.getSkillsById(skillsId)).thenReturn(skills);

        goalService.createGoal(onlyTitleGoalData, skillsId, null);

        verify(goalRepository, times(1)).save(captor.capture());

        Goal createdGoal = captor.getValue();
        Assertions.assertThat(createdGoal.getSkillsToAchieve()).containsExactlyElementsOf(skills);
    }

    @Test
    public void testUpdateNotAssociatedGoal() {
        Goal testGoal = Goal.builder()
                .users(List.of(
                        User.builder().id(USER_ID + 1).build(),
                        User.builder().id(USER_ID + 2).build(),
                        User.builder().id(USER_ID + 3).build()
                ))
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));

        assertThrows(UserNotGoalOwnerException.class, () -> goalService.update(GOAL_ID, new Goal(), new ArrayList<>()));
        verify(goalRepository).findById(GOAL_ID);
    }

    @Test
    public void testUpdateCompletedGoal() {
        Goal testGoal = Goal.builder()
                .users(List.of(user))
                .status(GoalStatus.COMPLETED)
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));

        assertThrows(UpdateComleteGoalException.class, () -> goalService.update(GOAL_ID, new Goal(), new ArrayList<>()));
        verify(goalRepository).findById(GOAL_ID);
    }

    @Test
    public void testUpdateActiveGoal() {
        String initialTitle = "Initial status";
        String newTitle = "New status";
        String initialDescription = "Initial description";
        String newDescription = "New description";
        LocalDateTime initialDeadline = LocalDateTime.now().plusDays(1);
        LocalDateTime newDeadline = LocalDateTime.now().plusDays(2);

        Goal testGoal = Goal.builder()
                .users(List.of(user))
                .title(initialTitle)
                .title(initialDescription)
                .status(GoalStatus.ACTIVE)
                .deadline(initialDeadline)
                .skillsToAchieve(new ArrayList<>())
                .build();

        Goal newGoalData = Goal.builder()
                .title(newTitle)
                .description(newDescription)
                .deadline(newDeadline)
                .build();

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));

        goalService.update(GOAL_ID, newGoalData, new ArrayList<>());
        verify(goalRepository).findById(GOAL_ID);
        verify(goalRepository, times(1)).save(captor.capture());

        Goal updatedGoal = captor.getValue();
        assertEquals(newTitle, updatedGoal.getTitle());
        assertEquals(newDescription, updatedGoal.getDescription());
        assertEquals(newDeadline, updatedGoal.getDeadline());
        assertEquals(testGoal.getStatus(), updatedGoal.getStatus());
        assertEquals(testGoal.getSkillsToAchieve(), updatedGoal.getSkillsToAchieve());

        verify(skillService, times(0)).removeSkillForGoal(GOAL_ID);
        verify(skillService, times(0)).getSkillsById(anyList());
    }

    @ParameterizedTest
    @MethodSource("skillsListProvider")
    public void testUpdateGoalSkillsSameSize(List<Long> initialSkillsId, List<Long> newSkillsId) {
        List<Skill> initialSkills = initialSkillsId.stream().map(ID_TO_SKILL).toList();
        List<Skill> newSkills = newSkillsId.stream().map(ID_TO_SKILL).toList();

        Goal testGoal = Goal.builder()
                .id(GOAL_ID)
                .users(List.of(user))
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(initialSkills)
                .build();

        Goal newGoalData = Goal.builder()
                .skillsToAchieve(newSkills)
                .build();

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));
        when(skillService.getSkillsById(newSkillsId)).thenReturn(newSkills);

        goalService.update(GOAL_ID, newGoalData, newSkillsId);
        verify(goalRepository).findById(GOAL_ID);
        verify(goalRepository, times(1)).save(captor.capture());

        Goal updatedGoal = captor.getValue();
        Assertions.assertThat(updatedGoal.getSkillsToAchieve()).containsExactlyElementsOf(newSkills);

        verify(skillService, times(1)).removeSkillForGoal(GOAL_ID);
        verify(skillService, times(1)).getSkillsById(anyList());
    }

    private static Stream<Arguments> skillsListProvider() {
        return Stream.of(
                Arguments.of(List.of(1L, 2L, 3L), List.of(2L, 3L, 4L)),
                Arguments.of(List.of(1L), List.of(1L, 2L, 3L)),
                Arguments.of(List.of(), List.of(1L, 3L, 4L)),
                Arguments.of(List.of(1L, 2L, 3L), List.of())
        );
    }

    @Test
    public void testCompleteGoal() {
        List<Long> usersId = List.of(1L, 2L, 3L);
        List<Long> skillsId = List.of(1L, 2L, 3L);
        List<Skill> skills = skillsId.stream().map(ID_TO_SKILL).toList();

        Goal testGoal = Goal.builder()
                .id(GOAL_ID)
                .users(List.of(user))
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(skills)
                .build();

        Goal newGoalData = Goal.builder()
                .status(GoalStatus.COMPLETED)
                .skillsToAchieve(skills)
                .build();

        ArgumentCaptor<Goal> captor = ArgumentCaptor.forClass(Goal.class);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.findUsersByGoalId(GOAL_ID)).thenReturn(usersId);

        goalService.update(GOAL_ID, newGoalData, skillsId);
        verify(goalRepository).findById(GOAL_ID);
        usersId.forEach(id -> verify(skillService).assignSkillsToUser(id, skills));
        verify(goalRepository, times(1)).save(captor.capture());

        Goal updatedGoal = captor.getValue();
        assertEquals(GoalStatus.COMPLETED, updatedGoal.getStatus());
    }

    @Test
    public void testDeleteNotAssociatedGoal() {
        Goal testGoal = Goal.builder()
                .users(List.of(
                        User.builder().id(USER_ID + 1).build(),
                        User.builder().id(USER_ID + 2).build(),
                        User.builder().id(USER_ID + 3).build()
                ))
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));
        lenient().when(goalRepository.findUsersByGoalId(GOAL_ID)).thenReturn(List.of(1L));

        assertThrows(UserNotGoalOwnerException.class, () -> goalService.delete(GOAL_ID));
        verify(goalRepository).findById(GOAL_ID);
    }

    @Test
    public void testDeleteGoalFromUser() {
        Goal testGoal = Goal.builder()
                .users(List.of(
                        User.builder().id(USER_ID).build(),
                        User.builder().id(USER_ID + 2).build(),
                        User.builder().id(USER_ID + 3).build()
                ))
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));
        lenient().when(goalRepository.findUsersByGoalId(GOAL_ID)).thenReturn(List.of(1L));

        assertDoesNotThrow(() -> goalService.delete(GOAL_ID));
        verify(goalRepository).findById(GOAL_ID);
        verify(goalRepository, times(1)).removeGoalFromUser(USER_ID, GOAL_ID);
    }

    @Test
    public void testDeleteGoalFromRepository() {
        List<Goal> goals = List.of(
                Goal.builder().id(1L).build(),
                Goal.builder().id(2L).build(),
                Goal.builder().id(3L).build()
        );
        Goal testGoal = Goal.builder()
                .users(List.of(
                        User.builder().id(USER_ID).build()
                ))
                .build();

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(goalRepository.findById(GOAL_ID)).thenReturn(Optional.of(testGoal));
        when(goalRepository.findUsersByGoalId(GOAL_ID)).thenReturn(new ArrayList<>());
        when(goalRepository.findByParent(GOAL_ID)).thenReturn(goals.stream());

        assertDoesNotThrow(() -> goalService.delete(GOAL_ID));
        verify(goalRepository).findById(GOAL_ID);
        verify(goalRepository, times(1)).removeGoalFromUser(USER_ID, GOAL_ID);
        goals.forEach(goal -> verify(goalRepository, times(1)).delete(goal));
        verify(goalRepository, times(1)).delete(testGoal);
    }
}