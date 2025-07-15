package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.Filter;
import school.faang.user_service.service.filter.FilterService;
import school.faang.user_service.service.filter.goal.GoalDescriptionFilter;
import school.faang.user_service.service.filter.goal.GoalFilterServiceImpl;
import school.faang.user_service.service.filter.goal.GoalMentorIdFilter;
import school.faang.user_service.service.filter.goal.GoalStatusFilter;
import school.faang.user_service.service.filter.goal.GoalTitleFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    private GoalDescriptionFilter descriptionFilter = new GoalDescriptionFilter();
    private GoalMentorIdFilter mentorIdFilter = new GoalMentorIdFilter();
    private GoalStatusFilter statusFilter = new GoalStatusFilter();
    private GoalTitleFilter titleFilter = new GoalTitleFilter();
    private List<Filter<Goal, GoalFilterDto>> filters = List.of(descriptionFilter, mentorIdFilter, statusFilter, titleFilter);
    @Mock
    private UserRepository userRepository;
    @Mock
    private GoalRepository goalRepository;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private UserContext userContext;

    @Spy
    private FilterService<Goal, GoalFilterDto> filterService = new GoalFilterServiceImpl(filters);
    @InjectMocks
    private GoalServiceImpl service;

    private static GoalDto defGoalDto;
    private static Goal defGoal;
    private static long currentUserId = 1L;

    @BeforeAll
    static void setUp() {
        defGoalDto = new GoalDto(
                1L,
                null,
                "title",
                "description",
                GoalStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                List.of(1L)
        );

        var users = new ArrayList<User>();
        for (Long id : defGoalDto.userIds()) {
            var user = new User();
            user.setId(id);
            users.add(user);
        }
        defGoal = new Goal();
        defGoal.setUsers(users);
        defGoal.setId(defGoalDto.id());
        defGoal.setTitle(defGoalDto.title());
        defGoal.setDescription(defGoalDto.description());
        defGoal.setDeadline(defGoalDto.deadline());
        defGoal.setStatus(defGoalDto.status());
    }

    @Test
    @DisplayName("create goal - success case")
    void create_success() {
        var createDto = new GoalCreateDto(
                null,
                defGoalDto.title(),
                defGoalDto.description(),
                defGoalDto.deadline(),
                null,
                defGoalDto.userIds()
        );

        var user = new User();
        user.setId(currentUserId);
        var goal = goalMapper.toGoal(createDto);
        goal.setUsers(List.of(user));
        goal.setId(1L);


        when(userContext.getUserId())
                .thenReturn(currentUserId);
        when(userRepository.getByIdOrThrow(currentUserId))
                .thenReturn(user);
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(goal);

        var actual = service.create(createDto);
        verify(userContext, times(2)).getUserId();
        verify(userRepository).getByIdOrThrow(currentUserId);
        assertEquals(defGoalDto, actual);
    }

    @Test
    @DisplayName("update goal - success case")
    void update_success() {
        var updateDto = new GoalUpdateDto(
                defGoalDto.title(),
                defGoalDto.description(),
                defGoalDto.deadline(),
                defGoalDto.mentorId(),
                defGoalDto.status()
        );

        var goalId = defGoalDto.id();
        var oldGoal = new Goal();
        oldGoal.setId(goalId);
        oldGoal.setUsers(defGoal.getUsers());

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(goalRepository.getByIdOrThrow(goalId))
                .thenReturn(oldGoal);
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(defGoal);
        var actual = service.update(oldGoal.getId(), updateDto);
        assertEquals(defGoalDto, actual);
    }

    @Test
    @DisplayName("get goal by id - success case")
    void getById_success() {
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(goalRepository.getByIdOrThrow(defGoal.getId()))
                .thenReturn(defGoal);

        var actual = service.getById(defGoal.getId());
        assertEquals(defGoalDto, actual);
    }

    @Test
    @DisplayName("delete goal - success case")
    void delete_success() {
        var goalId = defGoal.getId();
        when(userContext.getUserId()).thenReturn(currentUserId);
        when(goalRepository.getByIdOrThrow(goalId))
                .thenReturn(defGoal);
        service.delete(goalId);
        verify(goalRepository).deleteById(goalId);
    }

    @Test
    @DisplayName("get goals list by filters - success case")
    void getByFilters_success() {
        var expected = new ArrayList<GoalDto>();
        var filterDto = new GoalFilterDto(
                "no contains",
                "no has desctiption",
                null,
                null
        );

        when(userContext.getUserId()).thenReturn(currentUserId);
        when(goalRepository.findGoalsByUserId(currentUserId))
                .thenReturn(Stream.of(defGoal));
        var actual = service.getByFilters(filterDto);
        assertEquals(expected, actual);
    }
}