package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;
import static school.faang.user_service.entity.goal.GoalStatus.COMPLETED;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;
    private Long userId;
    private User user1;
    private User user2;
    private User mentor;
    private List<Skill> skillList;
    private List<Long> userIds;
    private List<Long> skillIds;
    private Long mentorId;
    private Skill skill1;
    private Skill skill2;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setId(1L);
        goal.setTitle("Test");
        goal.setStatus(ACTIVE);
        goal.setDescription("Description");
        goal.setDeadline(LocalDateTime.now().plusDays(30));

        user1 = new User();
        user1.setId(1L);
        user2 = new User();
        user2.setId(2L);

        skill1 = new Skill();
        skill1.setId(10L);
        skill2 = new Skill();
        skill2.setId(20L);
        skillList = Arrays.asList(skill1, skill2);
        mentorId = 3L;
        mentor = new User();
        mentor.setId(mentorId);

        userIds = Arrays.asList(1L, 2L);
        skillIds = Arrays.asList(10L, 20L);
    }

    @Test
    @DisplayName("Must successfully create a goal when all conditions are met.")
    public void create_ShouldSuccessfullyCreateGoal() {
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));
        when(skillRepository.findAllById(skillIds)).thenReturn(skillList);
        when(userRepository.getByIdOrThrow(mentorId)).thenReturn(mentor);

        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocationOnMock -> {
                    Goal goalSaved = invocationOnMock.getArgument(0);
                    goalSaved.setId(1L);
                    return goalSaved;
                });

        Goal result = goalService.create(goal, userIds, skillIds, mentor.getId());

        assertNotNull(result);
        assertEquals(mentor, result.getMentor());
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getTitle());
        assertEquals(skillList, result.getSkillsToAchieve());

        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(userRepository, times(1)).findAllById(userIds);
        verify(skillRepository, times(1)).findAllById(skillIds);
        verify(userRepository, times(1)).getByIdOrThrow(mentorId);
    }

    @Test
    @DisplayName("Must throw an exception when the user already has the maximum number of active targets.")
    public void create_ShouldErrorValidDate() {
        when(userRepository.findAllById(userIds)).thenReturn(Arrays.asList(user1, user2));

        Goal goal1 = new Goal();
        goal1.setTitle("Test Goal1");
        goal1.setDeadline(LocalDateTime.now().plusDays(30));
        goal1.setStatus(ACTIVE);

        Goal goal2 = new Goal();
        goal2.setTitle("Test Goal1");
        goal2.setDeadline(LocalDateTime.now().plusDays(30));
        goal2.setStatus(ACTIVE);
        List<Goal> goals = List.of(goal1, goal2);
        user1.setGoals(goals);

        assertThrows(DataValidationException.class,
                () -> goalService.create(goal, userIds, skillIds, mentorId));

        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    @DisplayName("Checking for goal deletion when the request was made by a mentor")
    public void delete_CheckForGoalFullRemove() {
        userId = 3L;
        Long goalId = 1L;
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    @DisplayName("Checking if a user's target has been deleted when the target has ONLY ONE user")
    public void delete_CheckForRemoveGoalHaveOneUser() {
        userId = 1L;
        Long goalId = 1L;
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    @DisplayName("Checking if a user's target has been deleted when the target has ANY user")
    public void delete_CheckForRemoveGoalHaveAnyUser() {
        userId = 1L;
        Long goalId = 1L;
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);

        goalService.delete(goalId);

        verify(goalRepository, times(1)).deleteUserFromGoal(userId, goalId);
    }

    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    @Test
    @DisplayName("Checks whether the target update was successful")
    public void update_successFull() {
        userId = 3L;
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        Long goalId = 1L;
        goal.setSkillsToAchieve(List.of(new Skill(), new Skill()));

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = 2L;
        GoalStatus goalStatus = null;
        List<Long> skillIds = null;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userRepository.getByIdOrThrow(mentorIdNew)).thenReturn(user2);
        when(userContext.getUserId()).thenReturn(userId);

        Goal goalUpdate = goalService.update(goalId, updateGoalDto);

        assertNotNull(goalUpdate);
        assertEquals(mentorIdNew, goalUpdate.getMentor().getId());
        assertEquals("testUpdate", goalUpdate.getTitle());
        assertEquals("descriptionUpdate", goalUpdate.getDescription());

        verify(goalRepository, times(1)).save(goal);
    }

    @Test
    @DisplayName("Check what happens if a user who is not a member or mentor tries to change the goal")
    public void update_ThisUserIsNotOnTheList() {
        userId = 2L;
        List<User> users = List.of(user1);
        goal.setUsers(users);
        goal.setMentor(mentor);
        Long goalId = 1L;

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = null;
        GoalStatus goalStatus = null;
        List<Long> skillIds = null;
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(2L);

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));
    }

    @Test
    @DisplayName("Check what happens if a target is being updated but it is already completed")
    public void update_GoalHaveStatusCompleted() {
        userId = 3L;
        List<User> users = List.of(user1, user2);
        goal.setUsers(users);
        goal.setMentor(mentor);
        goal.setStatus(COMPLETED);
        Long goalId = 1L;

        String title = "testUpdate";
        String description = "descriptionUpdate";
        LocalDateTime localDateTime = null;
        Long mentorIdNew = null;
        GoalStatus goalStatus = COMPLETED;
        List<Long> skillIds = null;

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(userId);
        UpdateGoalDto updateGoalDto = new UpdateGoalDto(title, description, localDateTime,
                mentorIdNew, goalStatus, skillIds);

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, updateGoalDto));
    }
}