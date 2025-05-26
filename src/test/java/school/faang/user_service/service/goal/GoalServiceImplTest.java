package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.UserServiceException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;
import java.util.*;
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
    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    private GoalFilter titleStubFilter = new GoalFilter() {
        @Override
        public boolean doFilter(Goal goal, GoalFilterDto filterDto) {
            return filterDto.getTitle().equals(goal.getTitle());
        }

        @Override
        public boolean isApplicable(GoalFilterDto criteria) {
            return true;
        }
    };

    private GoalFilter skillTitlesStubFilter = new GoalFilter() {
        @Override
        public boolean doFilter(Goal goal, GoalFilterDto criteria) {
            List<String> goalSkills = goal.getSkillsToAchieve().stream()
                    .map(Skill::getTitle)
                    .toList();
            return goalSkills.containsAll(criteria.getSkillTitles());
        }

        @Override
        public boolean isApplicable(GoalFilterDto criteria) {
            return true;
        }
    };

    private GoalServiceImpl goalService;

    @BeforeEach
    void setUp() {
        goalService = new GoalServiceImpl(
                goalMapper,
                goalRepository,
                skillRepository,
                userRepository,
                goalInvitationRepository,
                List.of(titleStubFilter, skillTitlesStubFilter)
        );
        ReflectionTestUtils.setField(goalService, "maximumAllowedActiveGoals", 3);
    }

    @Test
    void createGoalThrowsExceptionOnActiveGoalsLimitExceeded() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");

        GoalDto newActiveGoalDto = new GoalDto();
        newActiveGoalDto.setStatus(GoalStatus.ACTIVE);

        Goal activeGoal1 = new Goal();
        activeGoal1.setStatus(GoalStatus.ACTIVE);
        Goal activeGoal2 = new Goal();
        activeGoal2.setStatus(GoalStatus.ACTIVE);
        Goal activeGoal3 = new Goal();
        activeGoal3.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findGoalsByUserId(userId))
                .thenReturn(Stream.of(activeGoal1, activeGoal2, activeGoal3));

        UserServiceException exception = assertThrows(UserServiceException.class, () ->
                goalService.createGoal(userId, newActiveGoalDto)
        );

        assertEquals("User id: %d has Maximum allowed active goals - %d".formatted(userId, 3), exception.getMessage());

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

        List<Long> skillsIds = List.of(1L, 2L, 3L);

        String title = "New Goal";
        String description = "Description";
        GoalDto newGoalDto = new GoalDto();
        newGoalDto.setTitle(title);
        newGoalDto.setDescription(description);
        newGoalDto.setStatus(GoalStatus.ACTIVE);
        newGoalDto.setParentId(parentId);
        newGoalDto.setSkillIds(skillsIds);

        Goal savedGoal = new Goal();
        savedGoal.setId(100L);
        savedGoal.setTitle("New Goal");
        savedGoal.setDescription("Description");
        savedGoal.setStatus(GoalStatus.ACTIVE);
        savedGoal.setParent(parent);
        savedGoal.setUsers(List.of(user));
        savedGoal.setSkillsToAchieve(new ArrayList<>());

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of());
        when(goalRepository.create(title, description, parentId)).thenReturn(savedGoal);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(skillRepository.countExisting(skillsIds)).thenReturn(skillsIds.size());
        when(skillRepository.saveAllAndFlush(Collections.emptyList())).thenReturn(Collections.emptyList());

        GoalDto result = goalService.createGoal(userId, newGoalDto);

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

        GoalDto goalWithUnknownSkills = new GoalDto();
        goalWithUnknownSkills.setTitle("Test Goal");
        goalWithUnknownSkills.setDescription("Testing skill validation");
        goalWithUnknownSkills.setStatus(GoalStatus.ACTIVE);
        goalWithUnknownSkills.setParentId(parentId);
        goalWithUnknownSkills.setSkillIds(List.of(1L, 2L));

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of());
        when(skillRepository.countExisting(List.of(1L, 2L))).thenReturn(1); // skill2 отсутствует

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.createGoal(userId, goalWithUnknownSkills)
        );
        assertTrue(exception.getMessage().contains("Not existing skill ids provided"));

        verify(goalRepository, never()).create(anyString(), anyString(), any());
        verify(skillRepository, times(1)).countExisting(List.of(1L, 2L));
    }

    @Test
    void createGoalAddsGoalToUserGoalsList() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("test_user");
        user.setGoals(new ArrayList<>());

        Long parentId = 5L;
        Goal parent = new Goal();
        parent.setId(parentId);

        GoalDto newGoalDto = new GoalDto();
        newGoalDto.setTitle("Test Goal");
        newGoalDto.setDescription("Test Description");
        newGoalDto.setStatus(GoalStatus.ACTIVE);
        newGoalDto.setParentId(parentId);
        newGoalDto.setSkillIds(Collections.emptyList());

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
        when(goalRepository.create(newGoalDto.getTitle(), newGoalDto.getDescription(), parentId)).thenReturn(createdGoal);
        when(skillRepository.saveAllAndFlush(Collections.emptyList())).thenReturn(Collections.emptyList());

        goalService.createGoal(userId, newGoalDto);

        assertEquals(createdGoal, user.getGoals().get(0));

        verify(userRepository).findById(userId);
        verify(goalRepository).create(newGoalDto.getTitle(), newGoalDto.getDescription(), parentId);
        verify(skillRepository).saveAllAndFlush(Collections.emptyList());
    }

    @Test
    void updateGoalThrowsOnAlreadyCompletedGoal() {
        Long goalId = 1L;

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setStatus(GoalStatus.COMPLETED);

        GoalDto updateDto = new GoalDto();
        updateDto.setId(goalId);
        updateDto.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                goalService.updateGoal(goalId, updateDto)
        );

        assertEquals("Goal was already completed", exception.getMessage());

        verify(goalRepository, times(1)).findById(goalId);
        verify(goalMapper, never()).updateGoalFromDto(any(), any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    void updateGoalThrowsOnUnknownSkills() {
        Long goalId = 1L;

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setStatus(GoalStatus.ACTIVE);

        Long knownSkillId = 1L;
        Long unknownSkillId = 2L;

        GoalDto updateDto = new GoalDto();
        updateDto.setId(goalId);
        updateDto.setStatus(GoalStatus.ACTIVE);
        updateDto.setSkillIds(List.of(knownSkillId, unknownSkillId));

        Skill knownSkill = new Skill();
        knownSkill.setId(knownSkillId);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(updateDto.getSkillIds())).thenReturn(List.of(knownSkill));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.updateGoal(goalId, updateDto)
        );

        assertTrue(exception.getMessage().contains("Skill ids not exists:"));
        assertTrue(exception.getMessage().contains(unknownSkillId.toString()));

        verify(goalRepository).findById(goalId);
        verify(skillRepository).findAllById(updateDto.getSkillIds());
        verify(goalMapper, never()).updateGoalFromDto(any(), any());
        verify(goalRepository, never()).save(any());
    }

    @Test
    void updateGoalHappyPath() {
        Long goalId = 1L;

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setUpdatedAt(LocalDateTime.of(2025, 1, 1, 0, 0)); // старое значение

        Skill skill = new Skill();
        skill.setId(1L);

        GoalDto updateDto = new GoalDto();
        updateDto.setId(goalId);
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");
        updateDto.setStatus(GoalStatus.ACTIVE);
        updateDto.setSkillIds(List.of(skill.getId()));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(updateDto.getSkillIds())).thenReturn(List.of(skill));

        GoalDto resultDto = new GoalDto();
        resultDto.setId(goalId);
        when(goalMapper.toGoalDTO(existingGoal)).thenReturn(resultDto);

        GoalDto result = goalService.updateGoal(goalId, updateDto);

        assertEquals(goalId, result.getId());
        assertEquals("Updated Title", existingGoal.getTitle());
        assertEquals("Updated Description", existingGoal.getDescription());
        assertNotNull(existingGoal.getUpdatedAt());
        assertTrue(existingGoal.getUpdatedAt().isAfter(LocalDateTime.of(2025, 1, 1, 0, 0)));

        verify(goalRepository).findById(goalId);
        verify(skillRepository).findAllById(updateDto.getSkillIds());
        verify(goalMapper).updateGoalFromDto(updateDto, existingGoal);
        verify(goalRepository).save(existingGoal);
        verify(userRepository, never()).saveAllAndFlush(any());
        verify(skillRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void updateGoalCompletesGoalAndUpdatesUsersWithSkills() {
        Long goalId = 1L;

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(1L);
        user.setSkills(new ArrayList<>());

        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setSkillsToAchieve(List.of(skill));
        existingGoal.setUsers(List.of(user));

        GoalDto updateDto = new GoalDto();
        updateDto.setId(goalId);
        updateDto.setStatus(GoalStatus.COMPLETED);
        updateDto.setSkillIds(List.of(skill.getId()));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(updateDto.getSkillIds())).thenReturn(List.of(skill));
        when(goalMapper.toGoalDTO(existingGoal)).thenReturn(updateDto);

        goalService.updateGoal(goalId, updateDto);

        assertTrue(user.getSkills().contains(skill));
        assertTrue(skill.getUsers().contains(user));

        verify(userRepository).saveAllAndFlush(List.of(user));
        verify(skillRepository).saveAllAndFlush(List.of(skill));
    }

    @Test
    void deleteGoalThrowsIfGoalNotFound() {
        long goalId = 42L;
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> goalService.deleteGoal(goalId));

        verify(goalRepository).findById(goalId);
        verify(goalRepository, never()).delete(any());
        verify(userRepository, never()).saveAllAndFlush(any());
        verify(skillRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void deleteGoalRemovesGoalFromUsersAndSkills() {
        long goalId = 1L;

        Goal goal = new Goal();
        goal.setId(goalId);

        GoalDto expectedToDelete = new GoalDto();
        expectedToDelete.setDescription("to delete");

        User user = new User();
        user.setId(100L);
        user.setGoals(new ArrayList<>(List.of(goal)));
        goal.setUsers(List.of(user));

        Skill skill = new Skill();
        skill.setId(200L);
        skill.setGoals(new ArrayList<>(List.of(goal)));
        goal.setSkillsToAchieve(List.of(skill));

        GoalInvitation invitation = new GoalInvitation();
        invitation.setGoal(goal);
        goal.setInvitations(List.of(invitation));

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));
        when(goalMapper.toGoalDTO(goal)).thenReturn(expectedToDelete);

        GoalDto result = goalService.deleteGoal(goalId);

        assertFalse(user.getGoals().contains(goal));
        assertFalse(skill.getGoals().contains(goal));
        assertNull(invitation.getGoal());
        assertEquals(expectedToDelete, result);

        verify(goalRepository).findById(goalId);
        verify(goalRepository).delete(goal);
        verify(userRepository).saveAllAndFlush(List.of(user));
        verify(skillRepository).saveAllAndFlush(List.of(skill));
        verify(goalInvitationRepository).saveAllAndFlush(List.of(invitation));
        verify(goalMapper, atLeastOnce()).toGoalDTO(goal);
    }

    @Test
    void findSubtasksByGoalIdFiltered() {
        long parentGoalId = 1L;

        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("Expected Title");
        filterDto.setSkillTitles(List.of("Java", "Spring"));

        Skill javaSkill = new Skill();
        javaSkill.setTitle("Java");
        Skill springSkill = new Skill();
        springSkill.setTitle("Spring");
        Skill pythonSkill = new Skill();
        pythonSkill.setTitle("Python");

        Goal matchingGoal1 = new Goal();
        matchingGoal1.setId(101L);
        matchingGoal1.setTitle("Expected Title");
        matchingGoal1.setSkillsToAchieve(List.of(javaSkill, springSkill)); // pass

        Goal matchingGoal2 = new Goal();
        matchingGoal2.setId(102L);
        matchingGoal2.setTitle("Expected Title");
        matchingGoal2.setSkillsToAchieve(List.of(javaSkill, pythonSkill, springSkill)); // pass

        Goal nonMatchingByTitle = new Goal();
        nonMatchingByTitle.setId(103L);
        nonMatchingByTitle.setTitle("Wrong Title");
        nonMatchingByTitle.setSkillsToAchieve(List.of(javaSkill, springSkill)); // decline

        Goal nonMatchingBySkills = new Goal();
        nonMatchingBySkills.setId(104L);
        nonMatchingBySkills.setTitle("Expected Title");
        nonMatchingBySkills.setSkillsToAchieve(List.of(pythonSkill)); // decline

        Goal nonMatchingByBoth = new Goal();
        nonMatchingByBoth.setId(105L);
        nonMatchingByBoth.setTitle("Wrong Title");
        nonMatchingByBoth.setSkillsToAchieve(List.of(pythonSkill)); // decline

        Stream<Goal> allGoals = Stream.of(
                matchingGoal1,
                matchingGoal2,
                nonMatchingByTitle,
                nonMatchingBySkills,
                nonMatchingByBoth
        );

        when(goalRepository.findByParent(parentGoalId)).thenReturn(allGoals);

        GoalDto dto1 = goalMapper.toGoalDTO(matchingGoal1);
        GoalDto dto2 = goalMapper.toGoalDTO(matchingGoal2);

        List<GoalDto> result = goalService.findSubtasksByGoalId(parentGoalId, filterDto);

        assertEquals(List.of(dto1, dto2), result);

        verify(goalRepository).findByParent(parentGoalId);
        verify(goalMapper).toGoalDTOs(List.of(matchingGoal1, matchingGoal2));
    }

    @Test
    void findGoalsByUserIdFiltered() {
        long userId = 42L;

        GoalFilterDto filterDto = new GoalFilterDto();
        filterDto.setTitle("Expected Title");
        filterDto.setSkillTitles(List.of("Java", "Spring"));

        Skill javaSkill = new Skill();
        javaSkill.setTitle("Java");
        Skill springSkill = new Skill();
        springSkill.setTitle("Spring");
        Skill pythonSkill = new Skill();
        pythonSkill.setTitle("Python");

        Goal matchingGoal1 = new Goal();
        matchingGoal1.setId(201L);
        matchingGoal1.setTitle("Expected Title");
        matchingGoal1.setSkillsToAchieve(List.of(javaSkill, springSkill)); // pass

        Goal matchingGoal2 = new Goal();
        matchingGoal2.setId(202L);
        matchingGoal2.setTitle("Expected Title");
        matchingGoal2.setSkillsToAchieve(List.of(javaSkill, springSkill, pythonSkill)); // pass

        Goal nonMatchingByTitle = new Goal();
        nonMatchingByTitle.setId(203L);
        nonMatchingByTitle.setTitle("Wrong Title");
        nonMatchingByTitle.setSkillsToAchieve(List.of(javaSkill, springSkill)); // decline

        Goal nonMatchingBySkills = new Goal();
        nonMatchingBySkills.setId(204L);
        nonMatchingBySkills.setTitle("Expected Title");
        nonMatchingBySkills.setSkillsToAchieve(List.of(pythonSkill)); // decline

        Goal nonMatchingByBoth = new Goal();
        nonMatchingByBoth.setId(205L);
        nonMatchingByBoth.setTitle("Wrong Title");
        nonMatchingByBoth.setSkillsToAchieve(List.of(pythonSkill)); // decline

        Stream<Goal> allGoals = Stream.of(
                matchingGoal1,
                matchingGoal2,
                nonMatchingByTitle,
                nonMatchingBySkills,
                nonMatchingByBoth
        );

        when(goalRepository.findGoalsByUserId(userId)).thenReturn(allGoals);

        GoalDto dto1 = goalMapper.toGoalDTO(matchingGoal1);
        GoalDto dto2 = goalMapper.toGoalDTO(matchingGoal2);

        List<GoalDto> result = goalService.findGoalsByUserId(userId, filterDto);

        assertEquals(List.of(dto1, dto2), result);

        verify(goalRepository).findGoalsByUserId(userId);
        verify(goalMapper).toGoalDTOs(List.of(matchingGoal1, matchingGoal2));
    }

}