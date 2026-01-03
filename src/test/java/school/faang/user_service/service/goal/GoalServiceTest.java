package school.faang.user_service.service.goal;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalServiceImpl goalService;

    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private UserContext userContext;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;


    @Test
    public void testPositiveCreateGoalNotMentor() {
        long userId = 1L;
        CreateGoalDto createGoalDto = preparateGoalCreateDto("test", null, List.of());
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(userService.findById(userId)).thenReturn(User.builder().id(userId).build());
        when(skillService.findAllById(createGoalDto.getSkillsToAchieveIds())).thenReturn(List.of());

        goalService.create(createGoalDto);
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal result = goalCaptor.getValue();

        assertEquals("test", result.getTitle());
        assertEquals("test", result.getDescription());
        assertEquals(1L, result.getUsers().get(0).getId());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
        assertNull(result.getMentor());
    }

    @Test
    public void testPositiveCreateGoalWithMentor() {
        long mentorId = 1L;
        CreateGoalDto createGoalDto = preparateGoalCreateDto("test", mentorId, List.of(2L));
        when(userContext.getUserId()).thenReturn(mentorId);
        when(goalRepository.countActiveGoalsPerUser(2L)).thenReturn(1);
        when(skillService.findAllById(createGoalDto.getSkillsToAchieveIds())).thenReturn(List.of(
                Skill.builder().id(1L).build(),
                Skill.builder().id(2L).build()
        ));
        when(userService.findAllById(createGoalDto.getUserIds())).thenReturn(List.of(
                User.builder().id(2L).build()
        ));
        when(userService.findById(mentorId)).thenReturn(User.builder().id(1L).build());

        goalService.create(createGoalDto);
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal result = goalCaptor.getValue();

        assertEquals("test", result.getTitle());
        assertEquals("test", result.getDescription());
        assertEquals(1, result.getUsers().size());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
        assertEquals(mentorId, result.getMentor().getId());
    }

    @Test
    public void testIncorrectDataMentorId() {
        long userId = 1L;
        CreateGoalDto createGoalDto = preparateGoalCreateDto("test", userId, List.of());
        when(userContext.getUserId()).thenReturn(2L);
        assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));
    }

    @Test
    public void testThrowExceptionGoalMoreTwo() {
        long userId = 1L;
        CreateGoalDto createGoalDto = preparateGoalCreateDto("test", null, List.of());
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);
        assertThrows(IllegalStateException.class, () -> goalService.create(createGoalDto));
    }

    @Test
    public void testCheckingUserListEmpty() {
        long userId = 1L;
        CreateGoalDto createGoalDto = preparateGoalCreateDto("test", null, List.of(2L));
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        assertThrows(DataValidationException.class, () -> goalService.create(createGoalDto));
    }

    @Test
    public void testPositiveUpdateGoal() {
        long goalId = 1L;
        long userId = 2L;
        UpdateGoalDto updateGoalDto = preparateUpdateGoalDto("test1", "test1", 1L, GoalStatus.COMPLETED);
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .status(GoalStatus.ACTIVE)
                        .description("test0")
                        .users(List.of(User.builder().id(userId).build())).build())
        );
        goalService.update(goalId, updateGoalDto);
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal result = goalCaptor.getValue();
        assertEquals("test1", result.getDescription());
        assertEquals("test1", result.getTitle());
        assertEquals(GoalStatus.ACTIVE, result.getStatus());
    }

    @Test
    public void testThrowExceptionUpdateGoalComplete() {
        long goalId = 1L;
        long userId = 2L;
        UpdateGoalDto updateGoalDto = preparateUpdateGoalDto("test1", "test1", 1L, GoalStatus.ACTIVE);
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .status(GoalStatus.COMPLETED)
                        .description("test0")
                        .users(List.of(User.builder().id(userId).build())).build())
        );
        assertThrows(DataValidationException.class, () -> goalService.update(goalId, updateGoalDto));
    }

    @Test
    public void testThrowExceptionUpdateHaveGoalUser() {
        long goalId = 1L;
        long userId = 2L;
        long mentorId = 3L;
        UpdateGoalDto updateGoalDto = preparateUpdateGoalDto("test1", "test1", 1L, GoalStatus.ACTIVE);
        when(userContext.getUserId()).thenReturn(userId);
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .mentor(User.builder().id(mentorId).build())
                        .status(GoalStatus.ACTIVE)
                        .description("test0")
                        .users(List.of(User.builder().id(mentorId).build())).build())
        );

        assertThrows(DataValidationException.class, () -> goalService.update(goalId, updateGoalDto));
    }

    @Test
    public void testPositiveDeleteGoalRoleMentor() {
        long goalId = 1L;
        long mentorId = 3L;
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .mentor(User.builder().id(mentorId).build())
                        .status(GoalStatus.ACTIVE)
                        .description("test0")
                        .users(List.of(User.builder().build())).build())
        );
        when(userContext.getUserId()).thenReturn(mentorId);
        when(userService.findById(mentorId)).thenReturn(User.builder().id(mentorId).build());

        goalService.delete(goalId);
        verify(goalRepository, times(1)).delete(any());
    }

    @Test
    public void testPositiveDeleteGoalRolePersonalRole() {
        long goalId = 1L;
        long userId = 3L;
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .status(GoalStatus.ACTIVE)
                        .description("test0")
                        .users(List.of(User.builder().id(userId).build())).build())
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(User.builder().id(userId).build());

        goalService.delete(goalId);
        verify(goalRepository, times(1)).delete(any());
    }

    @Test
    public void testPositiveDeleteGoalRoleMentee() {
        long goalId = 1L;
        long userId = 3L;
        when(goalRepository.findById(goalId)).thenReturn(
                Optional.ofNullable(Goal.builder()
                        .id(goalId)
                        .status(GoalStatus.ACTIVE)
                        .description("test0")
                        .users(new ArrayList<>(List.of(
                                User.builder().id(userId).build(),
                                User.builder().id(5L).build())))
                        .build())
        );
        when(userContext.getUserId()).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(User.builder().id(userId).build());

        goalService.delete(goalId);
        verify(goalRepository, times(1)).save(goalCaptor.capture());
        Goal result = goalCaptor.getValue();
        assertEquals(1, result.getUsers().size());
        assertEquals(5L, result.getUsers().get(0).getId());
    }

    private UpdateGoalDto preparateUpdateGoalDto(String title, String description, Long mentorId, GoalStatus status) {
        return UpdateGoalDto.builder()
                .title(title)
                .description(description)
                .mentorId(mentorId)
                .status(status)
                .build();
    }

    private CreateGoalDto preparateGoalCreateDto(String title, Long mentorId, List<Long> userIds) {
        return CreateGoalDto.builder()
                .title(title)
                .description("test")
                .mentorId(mentorId)
                .userIds(userIds)
                .build();
    }
}