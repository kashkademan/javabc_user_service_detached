package school.faang.user_service.service.goal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalMapper goalMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private GoalServiceImpl goalService;


    @Test
    void testCreateGoalSuccess() {
        CreateGoalDto dto =
                new CreateGoalDto("Title", "Desc", LocalDateTime.now(), 1L, List.of(1L, 2L));
        Goal goal = new Goal();
        GoalDto goalDto =
                new GoalDto("Title", "Desc", LocalDateTime.now(), 1L,
                        List.of(1L, 2L), GoalStatus.ACTIVE);

        when(goalMapper.toGoal(dto)).thenReturn(goal);
        when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

        doNothing().when(validationService).validatePossibleToCreateOrUpdate(goal);

        GoalDto result = goalService.create(dto);

        assertEquals(goalDto, result);
        verify(goalRepository).save(goal);
        verify(validationService).validatePossibleToCreateOrUpdate(goal);
    }

    @Test
    void testCreateGoalForbidden() {
        CreateGoalDto dto =
                new CreateGoalDto("Title", "Desc", LocalDateTime.now(), 1L, List.of(1L, 2L));
        Goal goal = new Goal();

        when(goalMapper.toGoal(dto)).thenReturn(goal);

        doThrow(new ForbiddenException("You cannot create or update this goal"))
                .when(validationService).validatePossibleToCreateOrUpdate(goal);

        assertThrows(ForbiddenException.class, () -> goalService.create(dto));
        verify(goalRepository, never()).save(any());
        verify(validationService).validatePossibleToCreateOrUpdate(goal);
    }

    @Test
    void testUpdateGoalSuccess() {
        long goalId = 1L;
        UpdateGoalDto dto =
                new UpdateGoalDto("Title", "Desc", LocalDateTime.now(), 1L, GoalStatus.ACTIVE);
        Goal goal = new Goal();
        GoalDto goalDto =
                new GoalDto("Title", "Desc", LocalDateTime.now(), 1L,
                        List.of(1L, 2L), GoalStatus.ACTIVE);

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        doNothing().when(validationService).validatePossibleToCreateOrUpdate(goal);
        when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

        GoalDto result = goalService.update(goalId, dto);

        assertEquals(goalDto, result);
        verify(goalMapper).update(dto, goal);
        verify(validationService).validatePossibleToCreateOrUpdate(goal);
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testUpdateGoalForbidden() {
        long goalId = 1L;
        UpdateGoalDto dto = new UpdateGoalDto("Title", "Desc",
                LocalDateTime.now(), 1L, GoalStatus.ACTIVE);
        Goal goal = new Goal();

        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        doThrow(new ForbiddenException("You cannot create or update this goal"))
                .when(validationService).validatePossibleToCreateOrUpdate(goal);

        assertThrows(ForbiddenException.class, () -> goalService.update(goalId, dto));
        verify(goalMapper, never()).update(any(), any());
        verify(goalRepository, never()).save(any());
        verify(validationService).validatePossibleToCreateOrUpdate(goal);
    }


    @Test
    void testDeleteGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        User user = new User();
        user.setId(2L);
        user.setGoals(new ArrayList<>());
        goal.setUsers(new ArrayList<>(List.of(user)));

        when(goalRepository.getByIdOrThrow(1L)).thenReturn(goal);

        goalService.deleteGoal(1L);

        assertTrue(goal.getUsers().isEmpty());
        verify(goalRepository).delete(goal);
    }

    @Test
    void testDeleteGoalFromUser() {
        goalService.deleteGoalFromUser(1L, 2L);
        verify(goalRepository).deleteUserFromGoal(2L, 1L);
    }

    @Test
    void testGetByFilters() {
        Goal goal = new Goal();
        goal.setTitle("Test");
        goal.setDescription("Desc");
        goal.setStatus(GoalStatus.ACTIVE);
        User mentor = new User();
        mentor.setId(1L);
        mentor.setGoals(new ArrayList<>());
        goal.setMentor(mentor);
        goal.setUsers(new ArrayList<>());

        when(goalRepository.findAll()).thenReturn(List.of(goal));
        when(goalMapper.toGoalDto(goal))
                .thenReturn(new GoalDto("Test", "Desc",
                        LocalDateTime.now(), 1L, List.of(), GoalStatus.ACTIVE));

        GoalFilterDto filter = new GoalFilterDto("Test", "Desc",
                GoalStatus.ACTIVE, 1L);
        var result = goalService.getByFilters(filter);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).title());
    }
}