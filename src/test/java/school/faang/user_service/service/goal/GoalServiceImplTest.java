package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalService, "maximumAllowedActiveGoals", 3);
    }

    @Test
    void createGoalThrowsExceptionOnActiveGoalsLimitExceeded() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");

        Goal newActiveGoal = new Goal();
        newActiveGoal.setStatus(GoalStatus.ACTIVE);

        Goal activeGoal1 = new Goal();
        activeGoal1.setStatus(GoalStatus.ACTIVE);
        Goal activeGoal2 = new Goal();
        activeGoal2.setStatus(GoalStatus.ACTIVE);
        Goal activeGoal3 = new Goal();
        activeGoal3.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findGoalsByUserId(userId))
                .thenReturn(Stream.of(activeGoal1, activeGoal2, activeGoal3));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(userId, newActiveGoal)
        );

        assertEquals("User exceeded maximum allowed number or active goals " + 3, exception.getMessage());

        verify(goalRepository, never()).saveAndFlush(any());
    }

    @Test
    void createGoalCreatesNewGoalSuccessfully() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");
        user.setGoals(new ArrayList<>());

        Long parentId = 5L;
        Goal parent = new Goal();
        parent.setId(parentId);

        String title = "New Goal";
        String description = "Description";
        Goal newGoal = new Goal();
        newGoal.setTitle(title);
        newGoal.setDescription(description);
        newGoal.setStatus(GoalStatus.ACTIVE);
        newGoal.setParent(parent);
        newGoal.setSkillsToAchieve(new ArrayList<>());

        Goal savedGoal = new Goal();
        savedGoal.setId(100L);
        savedGoal.setTitle("New Goal");
        savedGoal.setDescription("Description");
        savedGoal.setStatus(GoalStatus.ACTIVE);
        newGoal.setParent(parent);
        savedGoal.setUsers(List.of(user));
        savedGoal.setSkillsToAchieve(new ArrayList<>());

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of());
        when(goalRepository.create(title, description, parentId)).thenReturn(savedGoal);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.saveAllAndFlush(Collections.emptyList())).thenReturn(Collections.emptyList());

        GoalDto result = goalService.createGoal(userId, newGoal);

        assertEquals(savedGoal.getId(), result.getId());

        verify(goalRepository, times(1)).create(anyString(), anyString(), anyLong());
        verify(userRepository, times(1)).findById(userId);
        verify(skillRepository, times(1)).saveAllAndFlush(Collections.emptyList());
    }

    @Test
    void createGoalThrowsExceptionOnUnknownSkills() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");

        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);

        Long parentId = 5L;
        Goal parent = new Goal();
        parent.setId(parentId);

        Goal goalWithUnknownSkills = new Goal();
        goalWithUnknownSkills.setTitle("Test Goal");
        goalWithUnknownSkills.setDescription("Testing skill validation");
        goalWithUnknownSkills.setStatus(GoalStatus.ACTIVE);
        goalWithUnknownSkills.setParent(parent);
        goalWithUnknownSkills.setSkillsToAchieve(List.of(skill1, skill2));

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of());
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill1)); // skill2 отсутствует

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(userId, goalWithUnknownSkills)
        );

        assertTrue(exception.getMessage().contains("User hasn't required skills for the goal: "));

        verify(goalRepository, never()).create(anyString(), anyString(), any());
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    void createGoalAddsGoalToUserGoalsList() {
        Long userId = 1L;
        Long parentId = 5L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");
        user.setGoals(new ArrayList<>());

        Goal parent = new Goal();
        parent.setId(parentId);

        Goal newGoal = new Goal();
        newGoal.setTitle("Test Goal");
        newGoal.setDescription("Test Description");
        newGoal.setStatus(GoalStatus.ACTIVE);
        newGoal.setParent(parent);
        newGoal.setSkillsToAchieve(Collections.emptyList());

        Goal createdGoal = new Goal();
        createdGoal.setId(10L);
        createdGoal.setTitle("Test Goal");
        createdGoal.setDescription("Test Description");
        createdGoal.setStatus(GoalStatus.ACTIVE);
        createdGoal.setParent(parent);
        createdGoal.setSkillsToAchieve(Collections.emptyList());
        createdGoal.setUsers(List.of(user));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of());
        when(goalRepository.create(newGoal.getTitle(), newGoal.getDescription(), parentId)).thenReturn(createdGoal);
        when(skillRepository.saveAllAndFlush(Collections.emptyList())).thenReturn(Collections.emptyList());

        goalService.createGoal(userId, newGoal);

        assertEquals(createdGoal, user.getGoals().get(0));

        verify(userRepository).findById(userId);
        verify(goalRepository).create(newGoal.getTitle(), newGoal.getDescription(), parentId);
        verify(skillRepository).saveAllAndFlush(Collections.emptyList());
    }


}