package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private GoalService goalService;

    private Goal goal;
    private User user1;
    private User user2;
    private User mentor;
    private List<User> userList;
    private List<Skill> skillList;

    @BeforeEach
    public void setUp() {
        goal = new Goal();
        goal.setTitle("Test Goal");
        goal.setDeadline(LocalDateTime.now().plusDays(30));

        user1 = new User();
        user1.setId(1L);

        user2 = new User();
        user2.setId(2L);

        mentor = new User();
        mentor.setId(3L);

        userList = Arrays.asList(user1, user2);
        skillList = Arrays.asList(new Skill(), new Skill());
    }

    @Test
    @DisplayName("Must successfully create a goal when all conditions are met.")
    public void create_ShouldSuccessfullyCreateGoal() {
        when(goalRepository.save(any(Goal.class)))
                .thenAnswer(invocationOnMock -> {
                    Goal goalSaved = invocationOnMock.getArgument(0);
                    goalSaved.setId(1L);
                    return goalSaved;
                });

        System.out.println(userList);
        Goal result = goalService.create(goal, userList, skillList, mentor);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Goal", result.getTitle());
        assertEquals(skillList, result.getSkillsToAchieve());
        assertEquals(mentor, result.getMentor());
        assertEquals(userList, result.getUsers());
        assertEquals(ACTIVE, result.getStatus());

        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    @DisplayName("Must throw an exception when the user already has the maximum number of active targets.")
    public void create_ShouldErrorValidDate() {
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
                () -> goalService.create(goal, userList, skillList, mentor));

        verify(goalRepository, never()).save(any(Goal.class));
    }
}