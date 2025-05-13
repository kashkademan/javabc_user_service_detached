package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private UserContext userContext;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;
    @InjectMocks
    private GoalService goalService;

    private GoalCreateRequestDto goalCreateRequestDto;

    private GoalUpdateRequestDto goalUpdateRequestDto;

    @BeforeEach
    void setUp() {
        String title = "Test Goal";
        String description = "Test description";
        List<Long> skillIds = List.of(1L);

        goalCreateRequestDto = new GoalCreateRequestDto();
        goalCreateRequestDto.setTitle(title);
        goalCreateRequestDto.setDescription(description);
        goalCreateRequestDto.setSkillIds(skillIds);

        goalUpdateRequestDto = new GoalUpdateRequestDto();
        goalUpdateRequestDto.setTitle("Test Goal");
        goalUpdateRequestDto.setDescription("Test description");
        goalUpdateRequestDto.setSkillIds(skillIds);
        goalUpdateRequestDto.setStatus(GoalStatus.ACTIVE);
    }


    @Test
    public void testCreateGoal_savesGoalNonParent() {
        Goal goal = goalMapper.toGoalEntity(goalCreateRequestDto);
        long goalId = 10L;
        goal.setId(goalId);

        when(goalRepository.save(any())).thenReturn(goal);

        GoalResponseDto goalResponseDto = goalService.createGoal(goalCreateRequestDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        assertEquals(goalId, goalResponseDto.getId());
        assertEquals(goalCreateRequestDto.getTitle(), goalResponseDto.getTitle());
        assertEquals(goalCreateRequestDto.getDescription(), goalResponseDto.getDescription());
        assertEquals(goalCreateRequestDto.getParentId(), goalResponseDto.getParentId());
    }

    @Test
    public void testCreateGoal_savesGoalWithParent() {
        long goalId = 10L;
        long goalParentId = 5L;
        goalCreateRequestDto.setParentId(goalParentId);
        Goal parentGoal = new Goal();
        parentGoal.setId(goalParentId);

        Goal goal = goalMapper.toGoalEntity(goalCreateRequestDto);
        goal.setId(goalId);
        goal.setParent(parentGoal);

        when(goalRepository.findById(goalCreateRequestDto.getParentId())).thenReturn(Optional.of(parentGoal));
        when(goalRepository.save(any())).thenReturn(goal);

        GoalResponseDto goalResponseDto = goalService.createGoal(goalCreateRequestDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        assertEquals(goalId, goalResponseDto.getId());
        assertEquals(goalCreateRequestDto.getTitle(), goalResponseDto.getTitle());
        assertEquals(goalCreateRequestDto.getDescription(), goalResponseDto.getDescription());
        assertEquals(goalCreateRequestDto.getParentId(), goalResponseDto.getParentId());
    }

    @Test
    public void testCreateGoal_countActiveGoalMoreMax() {
        long userId = 1L;
        int countActiveGoalsPerUser = 4;
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);

        assertThrows(CountActiveGoalMoreMaxException.class, () -> goalService.createGoal(goalCreateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testCreateGoal_nonExistingSkill() {
        goalCreateRequestDto.setSkillIds(Arrays.asList(12L, 15L));
        long userId = 1L;
        int countActiveGoalsPerUser = 3;
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(countActiveGoalsPerUser);
        when(skillService.getSkillByIdOrThrow(goalCreateRequestDto.getSkillIds().get(0)))
                .thenThrow(SkillNotFoundException.class);

        assertThrows(SkillNotFoundException.class, () -> goalService.createGoal(goalCreateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testCreateGoal_nonExistingParentGoal() {
        goalCreateRequestDto.setParentId(2L);
        when(goalRepository.findById(goalCreateRequestDto.getParentId())).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () -> goalService.createGoal(goalCreateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
    }

    @Test
    public void testUpdateGoal_savesGoalActive() {
        long goalId = 1L;
        goalUpdateRequestDto.setId(goalId);

        Goal saveGoal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        saveGoal.setSkillsToAchieve(new ArrayList<>());
        saveGoal.setUsers(new ArrayList<>());
        saveGoal.setInvitations(new ArrayList<>());

        Skill mockSkill = new Skill();
        mockSkill.setId(1L);

        when(skillService.getSkillByIdOrThrow(goalUpdateRequestDto.getSkillIds().get(0)))
                .thenReturn(mockSkill);
        Goal goal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(saveGoal);

        GoalResponseDto goalResponseDto = goalService.updateGoal(goalUpdateRequestDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(skillService, never()).assignSkillsToUsers(any(), any());
        assertEquals(goalId, goalResponseDto.getId());
        assertEquals(goalUpdateRequestDto.getTitle(), goalResponseDto.getTitle());
        assertEquals(goalUpdateRequestDto.getDescription(), goalResponseDto.getDescription());
        assertEquals(goalUpdateRequestDto.getStatus(), goalResponseDto.getStatus());
    }

    //TODO: не нужно when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal)), это не верно
    @Test
    public void testUpdateGoal_savesGoalCompleted() {
        long goalId = 1L;
        goalUpdateRequestDto.setId(goalId);

        Goal saveGoal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        saveGoal.setSkillsToAchieve(new ArrayList<>());
        saveGoal.setUsers(new ArrayList<>());
        saveGoal.setInvitations(new ArrayList<>());
        saveGoal.setStatus(GoalStatus.COMPLETED);

        Skill mockSkill = new Skill();
        mockSkill.setId(1L);

        when(skillService.getSkillByIdOrThrow(goalUpdateRequestDto.getSkillIds().get(0)))
                .thenReturn(mockSkill);
        Goal goal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(saveGoal);

        GoalResponseDto goalResponseDto = goalService.updateGoal(goalUpdateRequestDto);

        verify(goalRepository, times(1)).save(goalCaptor.capture());
        verify(skillService).assignSkillsToUsers(any(), any());
        assertEquals(goalId, goalResponseDto.getId());
        assertEquals(goalUpdateRequestDto.getTitle(), goalResponseDto.getTitle());
        assertEquals(goalUpdateRequestDto.getDescription(), goalResponseDto.getDescription());
//        assertEquals(goalUpdateRequestDto.getStatus(), goalResponseDto.getStatus());
    }

    @Test
    public void testUpdateGoal_goalNotFound() {
        long goalId = 1L;
        goalUpdateRequestDto.setId(goalId);
        when(goalRepository.findById(goalId)).thenThrow(GoalNotFoundException.class);

        assertThrows(GoalNotFoundException.class, () -> goalService.updateGoal(goalUpdateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
        verify(skillService, never()).assignSkillsToUsers(any(), any());
    }

    @Test
    public void testUpdateGoal_goalAlreadyCompleted() {
        long goalId = 1L;
        goalUpdateRequestDto.setId(goalId);
        goalUpdateRequestDto.setStatus(GoalStatus.COMPLETED);

        Goal goal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(GoalAlreadyCompletedException.class, () -> goalService.updateGoal(goalUpdateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
        verify(skillService, never()).assignSkillsToUsers(any(), any());
    }

    @Test
    public void testUpdateGoal_nonExistingSkill() {
        long goalId = 1L;
        goalUpdateRequestDto.setId(goalId);
        goalUpdateRequestDto.setSkillIds(Arrays.asList(12L, 15L));

        Goal goal = goalMapper.toGoalEntity(goalUpdateRequestDto);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        when(skillService.getSkillByIdOrThrow(goalUpdateRequestDto.getSkillIds().get(0)))
                .thenThrow(SkillNotFoundException.class);

        assertThrows(SkillNotFoundException.class, () -> goalService.updateGoal(goalUpdateRequestDto));
        verify(goalRepository, never()).save(goalCaptor.capture());
        verify(skillService, never()).assignSkillsToUsers(any(), any());
    }
}
