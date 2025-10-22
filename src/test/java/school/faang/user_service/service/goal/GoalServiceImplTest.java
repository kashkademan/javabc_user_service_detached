package school.faang.user_service.service.goal;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
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

    @InjectMocks
    private GoalServiceImpl goalService;

    @Test
    void testCreateGoalSuccess() {
        CreateGoalDto dto = new CreateGoalDto("Title", "Desc", LocalDateTime.now(), 1L, List.of(1L, 2L));

        User mentor = new User();
        mentor.setId(1L);
        mentor.setGoals(new ArrayList<>());

        User user1 = new User();
        user1.setId(1L);
        user1.setGoals(new ArrayList<>());

        User user2 = new User();
        user2.setId(2L);
        user2.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>(List.of(user1, user2)));
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setMentor(mentor);

        GoalDto goalDto = new GoalDto("Title", "Desc", LocalDateTime.now(),
                1L, List.of(1L, 2L), GoalStatus.ACTIVE);

        when(goalMapper.toGoal(dto)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.findAllByIdIn(any())).thenReturn(List.of(user1, user2));
        when(goalMapper.toGoalDto(goal)).thenReturn(goalDto);

        GoalDto result = goalService.create(dto);

        assertEquals(goalDto, result);
        verify(goalRepository).save(goal);
    }


    @Test
    void testCreateGoalForbidden() {
        CreateGoalDto dto = new CreateGoalDto("Title", "Desc",
                LocalDateTime.now(), 1L, List.of(2L));
        User user = new User();
        user.setId(2L);
        user.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setUsers(new ArrayList<>(List.of(user)));
        goal.setStatus(GoalStatus.ACTIVE);

        when(goalMapper.toGoal(dto)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(999L);
        when(userRepository.findAllByIdIn(any())).thenReturn(List.of(user));

        assertThrows(ForbiddenException.class, () -> goalService.create(dto));
        verify(goalRepository, never()).save(any());
    }

    @Test
    void testUpdateGoalSuccess() {
        UpdateGoalDto dto = new UpdateGoalDto("Title", "Desc", LocalDateTime.now(), 1L, GoalStatus.ACTIVE);

        User mentor = new User();
        mentor.setId(2L);
        mentor.setGoals(new ArrayList<>());

        Goal goal = new Goal();
        goal.setId(1L);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setMentor(mentor);

        User user1 = new User();
        user1.setId(2L);
        user1.setGoals(new ArrayList<>());

        User user2 = new User();
        user2.setId(3L);
        user2.setGoals(new ArrayList<>());

        goal.setUsers(new ArrayList<>(List.of(user1, user2)));

        when(goalRepository.getByIdOrThrow(1L)).thenReturn(goal);
        when(userContext.getUserId()).thenReturn(2L);
        when(userRepository.findAllByIdIn(any())).thenReturn(List.of(user1, user2));
        when(goalMapper.toGoalDto(goal)).
                thenReturn(new GoalDto("Title", "Desc",
                        LocalDateTime.now(), 1L, List.of(2L, 3L), GoalStatus.ACTIVE));

        GoalDto result = goalService.update(1L, dto);

        assertNotNull(result);
        verify(goalMapper).update(dto, goal);
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
        when(goalMapper.toGoalDto(goal)).thenReturn(new GoalDto("Test", "Desc", LocalDateTime.now(), 1L, List.of(), GoalStatus.ACTIVE));

        GoalFilterDto filter = new GoalFilterDto("Test", "Desc", GoalStatus.ACTIVE, 1L);
        var result = goalService.getByFilters(filter);

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).title());
    }
}