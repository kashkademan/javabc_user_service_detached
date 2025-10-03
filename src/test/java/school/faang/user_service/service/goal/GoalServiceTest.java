package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.helpers.TestUtils;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.repository.user.UserSkillGuaranteeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private GoalMapper goalMapper = new GoalMapperImplSpy();
    @Mock
    private UserContext userContext;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @InjectMocks
    private GoalService goalService;
    @Captor
    private ArgumentCaptor<Goal> goalCaptor;
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> guaranteesCaptor;

    @ParameterizedTest
    @MethodSource("invalidDeadlines")
    void create_shouldThrowException_whenDeadlineIsInvalid(final LocalDateTime invalidDeadline) {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                invalidDeadline,
                null,
                List.of(1L),
                List.of(1L),
                null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Deadline date should provide at least 1 day for achievement",
                () -> goalService.create(createGoalDto)
        );
    }

    private static Stream<LocalDateTime> invalidDeadlines() {
        return Stream.of(
                LocalDateTime.now().plusDays(1).minusMinutes(1),
                LocalDateTime.now().plusDays(1).minusHours(1),
                LocalDateTime.now().plusDays(1).minusSeconds(1),
                LocalDateTime.now().plusHours(12)
        );
    }

    @Test
    void create_shouldThrowException_whenMentorIdAndUserIdHeaderAreDifferent() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                1L,
                List.of(1L),
                List.of(1L),
                null
        );

        when(userContext.getUserId()).thenReturn(2L);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "MentorId 1 and requesterId 2 must be the same",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenUserIdsContainMentorId() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                1L,
                List.of(1L),
                List.of(1L),
                null
        );

        when(userContext.getUserId()).thenReturn(1L);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Mentor cannot create goal for himself",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenMentorIdIsEmptyAndUserIdsSizeMoreThanOne() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(1L, 2L),
                List.of(1L),
                null
        );

        when(userContext.getUserId()).thenReturn(1L);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "When mentorId is empty, userIds size must be 1, actual: 2",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenMentorIdIsEmptyAndUserIdsNotMatchRequesterId() {
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(1L),
                List.of(1L),
                null
        );

        when(userContext.getUserId()).thenReturn(2L);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "RequesterId 2 and userIds first id 1 must be the same",
                () -> goalService.create(createGoalDto)
        );
    }


    @Test
    void create_shouldThrowException_whenUserHasMoreThanTwoActiveGoals() {
        final Long requesterId = 1L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(requesterId),
                List.of(1L),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(2);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "User has more than 2 active goals: 2",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldSaveGoal_whenMentorParentAndSkillsAreEmpty() {
        final Long requesterId = 10L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(requesterId),
                null,
                null
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(User.builder().id(requesterId).build()));

        GoalDto result = goalService.create(createGoalDto);

        assertNull(result.id());
        assertNull(result.createdAt());
        assertNull(result.updatedAt());
        assertEquals(createGoalDto.title(), result.title());
        assertEquals(createGoalDto.description(), result.description());
        assertEquals(createGoalDto.deadline(), result.deadline());
        assertNull(result.mentorId());
        assertEquals(1, result.userIds().size());
        assertEquals(requesterId, result.userIds().get(0));
        assertEquals(GoalStatus.ACTIVE, result.status());
        assertNull(result.parentGoalId());

        verify(goalRepository).save(goalCaptor.capture());
        final Goal saved = goalCaptor.getValue();

        assertNotNull(saved);
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getMentor());
        assertNull(saved.getParent());
        assertTrue(saved.getUsers() != null && saved.getUsers().size() == 1);
        assertEquals(requesterId, saved.getUsers().get(0).getId());
        assertTrue(saved.getSkillsToAchieve() == null || saved.getSkillsToAchieve().isEmpty());

        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void create_shouldThrowException_whenUserNotFoundByIdInMentorFlow() {
        final Long requesterMentorId = 30L;
        final Long missingUserId = 99L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                requesterMentorId,
                List.of(missingUserId),
                null,
                null
        );

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.countActiveGoalsPerUser(missingUserId)).thenReturn(0);
        when(userRepository.findById(requesterMentorId))
                .thenReturn(Optional.of(User.builder().id(requesterMentorId).build()));
        when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "User not found by id: 99",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenSkillNotFoundById() {
        final Long requesterId = 40L;
        final Long missingSkillId = 404L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(requesterId),
                List.of(missingSkillId),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(skillRepository.findById(missingSkillId)).thenReturn(Optional.empty());

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Skill not found by id: 404",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenParentGoalNotFoundById() {
        final Long requesterId = 50L;
        final Long missingParentGoalId = 777L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(2),
                null,
                List.of(requesterId),
                null,
                missingParentGoalId
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(goalRepository.findById(missingParentGoalId)).thenReturn(Optional.empty());

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Parent Goal not found by id: 777",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldSaveGoalWithParent_whenParentGoalProvidedAndNoMentor() {
        final Long requesterId = 11L;
        final Long parentGoalId = 100L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(3),
                null,
                List.of(requesterId),
                null,
                parentGoalId
        );

        Goal parent = Goal.builder().id(parentGoalId).build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(User.builder().id(requesterId).build()));
        when(goalRepository.findById(parentGoalId)).thenReturn(Optional.of(parent));

        GoalDto result = goalService.create(createGoalDto);

        assertEquals(createGoalDto.title(), result.title());
        assertEquals(createGoalDto.description(), result.description());
        assertEquals(createGoalDto.deadline(), result.deadline());
        assertNull(result.mentorId());
        assertEquals(1, result.userIds().size());
        assertEquals(requesterId, result.userIds().get(0));
        assertEquals(GoalStatus.ACTIVE, result.status());
        assertEquals(parentGoalId, result.parentGoalId());

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();

        assertNotNull(saved);
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getMentor());
        assertNotNull(saved.getParent());
        assertEquals(parentGoalId, saved.getParent().getId());
        assertTrue(saved.getUsers() != null && saved.getUsers().size() == 1);
        assertEquals(requesterId, saved.getUsers().get(0).getId());
        assertTrue(saved.getSkillsToAchieve() == null || saved.getSkillsToAchieve().isEmpty());

        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void create_shouldSaveGoalWithSkills_whenSkillsProvidedAndNoMentor() {
        final Long requesterId = 12L;
        final Long skillId1 = 201L;
        final Long skillId2 = 202L;
        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(4),
                null,
                List.of(requesterId),
                List.of(skillId1, skillId2),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(User.builder().id(requesterId).build()));
        when(skillRepository.findById(skillId1)).thenReturn(Optional.of(Skill.builder().id(skillId1).build()));
        when(skillRepository.findById(skillId2)).thenReturn(Optional.of(Skill.builder().id(skillId2).build()));

        GoalDto result = goalService.create(createGoalDto);

        assertEquals(createGoalDto.title(), result.title());
        assertEquals(createGoalDto.description(), result.description());
        assertEquals(createGoalDto.deadline(), result.deadline());
        assertNull(result.mentorId());
        assertEquals(1, result.userIds().size());
        assertEquals(requesterId, result.userIds().get(0));
        assertEquals(GoalStatus.ACTIVE, result.status());
        assertNull(result.parentGoalId());

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();

        assertNotNull(saved);
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getMentor());
        assertNull(saved.getParent());
        assertTrue(saved.getUsers() != null && saved.getUsers().size() == 1);
        assertEquals(requesterId, saved.getUsers().get(0).getId());
        assertTrue(saved.getSkillsToAchieve() != null && saved.getSkillsToAchieve().size() == 2);
        assertEquals(skillId1, saved.getSkillsToAchieve().get(0).getId());
        assertEquals(skillId2, saved.getSkillsToAchieve().get(1).getId());

        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void create_shouldSaveGoalWithMentorUsersAndSkills_andCreateGuarantees() {
        final Long requesterMentorId = 20L;
        final Long userId1 = 21L;
        final Long userId2 = 22L;
        final Long skillId1 = 301L;
        final Long skillId2 = 302L;

        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(5),
                requesterMentorId,
                List.of(userId1, userId2),
                List.of(skillId1, skillId2),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.countActiveGoalsPerUser(userId1)).thenReturn(0);
        when(goalRepository.countActiveGoalsPerUser(userId2)).thenReturn(0);
        when(userRepository.findById(requesterMentorId))
                .thenReturn(Optional.of(User.builder().id(requesterMentorId).build()));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(User.builder().id(userId1).build()));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(User.builder().id(userId2).build()));
        when(skillRepository.findById(skillId1)).thenReturn(Optional.of(Skill.builder().id(skillId1).build()));
        when(skillRepository.findById(skillId2)).thenReturn(Optional.of(Skill.builder().id(skillId2).build()));

        GoalDto result = goalService.create(createGoalDto);

        assertEquals(createGoalDto.title(), result.title());
        assertEquals(createGoalDto.description(), result.description());
        assertEquals(createGoalDto.deadline(), result.deadline());
        assertEquals(requesterMentorId, result.mentorId());
        assertEquals(2, result.userIds().size());
        assertEquals(userId1, result.userIds().get(0));
        assertEquals(userId2, result.userIds().get(1));
        assertEquals(GoalStatus.ACTIVE, result.status());
        assertNull(result.parentGoalId());

        verify(userSkillGuaranteeRepository).saveAll(guaranteesCaptor.capture());
        List<UserSkillGuarantee> guarantees = guaranteesCaptor.getValue();
        assertNotNull(guarantees);
        assertEquals(4, guarantees.size());

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();
        assertNotNull(saved);
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getMentor());
        assertEquals(requesterMentorId, saved.getMentor().getId());
        assertTrue(saved.getUsers() != null && saved.getUsers().size() == 2);
        assertNull(saved.getParent());
        assertTrue(saved.getSkillsToAchieve() != null && saved.getSkillsToAchieve().size() == 2);
    }

    @Test
    void create_shouldThrowException_whenUserIdsAreNotUnique() {
        final Long requesterMentorId = 1L;
        final Long userId1 = 2L;
        final Long userId2 = 2L;
        final Long skillId1 = 3L;
        final Long skillId2 = 4L;

        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(5),
                requesterMentorId,
                List.of(userId1, userId2),
                List.of(skillId1, skillId2),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterMentorId);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "UserIds must contain only unique values: [2, 2]",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void create_shouldThrowException_whenSkillIdsAreNotUnique() {
        final Long requesterMentorId = 1L;
        final Long userId1 = 2L;
        final Long userId2 = 3L;
        final Long skillId1 = 4L;
        final Long skillId2 = 4L;

        final CreateGoalDto createGoalDto = new CreateGoalDto(
                "title",
                "description",
                LocalDateTime.now().plusDays(5),
                requesterMentorId,
                List.of(userId1, userId2),
                List.of(skillId1, skillId2),
                null
        );

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.countActiveGoalsPerUser(userId1)).thenReturn(0);
        when(goalRepository.countActiveGoalsPerUser(userId2)).thenReturn(0);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "SkillIds must contain only unique values: [4, 4]",
                () -> goalService.create(createGoalDto)
        );
    }

    @Test
    void delete_shouldThrowException_whenGoalIsParent() {
        final long goalId = 1L;
        final long requesterId = 10L;

        Goal goal = Goal.builder()
                .id(goalId)
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(Goal.builder().id(2L).build()));

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Goal %s is parent and cannot be delete".formatted(goalId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldThrowException_whenRequesterIsNotParticipant_andNoMentor() {
        final long goalId = 2L;
        final long requesterId = 20L;

        Goal goal = Goal.builder()
                .id(goalId)
                .users(List.of(User.builder().id(999L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User %s cannot delete goal %s".formatted(requesterId, goalId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldThrowException_whenRequesterIsNotMentor() {
        final long goalId = 3L;
        final long requesterId = 30L;
        final long mentorId = 300L;

        Goal goal = Goal.builder()
                .id(goalId)
                .mentor(User.builder().id(mentorId).build())
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "MentorId %s and requesterId %s are different".formatted(mentorId, requesterId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldDeleteById_whenNoMentor_andSingleParticipant() {
        final long goalId = 4L;
        final long requesterId = 40L;

        Goal goal = Goal.builder()
                .id(goalId)
                .users(List.of(User.builder().id(requesterId).build()))
                .skillsToAchieve(List.of())
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        goalService.delete(goalId);

        verify(goalRepository).deleteById(goalId);
        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void delete_shouldRemoveUserFromGoal_whenNoMentor_andMultipleParticipants() {
        final long goalId = 5L;
        final long requesterId = 50L;

        Goal goal = Goal.builder()
                .id(goalId)
                .users(List.of(
                        User.builder().id(requesterId).build(),
                        User.builder().id(501L).build()
                ))
                .skillsToAchieve(List.of())
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        goalService.delete(goalId);

        verify(goalRepository).deleteUserFromGoal(requesterId, goalId);
        verify(goalRepository, never()).deleteById(anyLong());
        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void delete_shouldDeleteByIdAndGuarantees_whenMentor_andSkillsPresent() {
        final long goalId = 6L;
        final long requesterMentorId = 60L;

        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();

        Goal goal = Goal.builder()
                .id(goalId)
                .mentor(User.builder().id(requesterMentorId).build())
                .users(List.of(User.builder().id(601L).build()))
                .skillsToAchieve(List.of(skill1, skill2))
                .build();

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.empty());

        goalService.delete(goalId);

        verify(goalRepository).deleteById(goalId);
        verify(userSkillGuaranteeRepository).deleteBySkillIdIn(List.of(1L, 2L));
    }

    @Test
    void delete_shouldPropagateEntityNotFound_whenGoalDoesNotExist() {
        final long goalId = 7L;
        final long requesterId = 70L;

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenThrow(
                new EntityNotFoundException("Goal %d not found".formatted(goalId))
        );

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Goal %d not found".formatted(goalId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void getByFilters_shouldReturnAllGoals_whenNoFiltersProvided() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Description 2")
                .status(GoalStatus.COMPLETED)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto(null, null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(2, result.size());
    }

    @Test
    void getByFilters_shouldFilterByTitleContains() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Java Learning")
                .description("Learn Java")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Python Learning")
                .description("Learn Python")
                .status(GoalStatus.ACTIVE)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto("Java", null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals("Java Learning", result.get(0).title());
    }

    @Test
    void getByFilters_shouldFilterByDescriptionContains() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Learn programming basics")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Learn database design")
                .status(GoalStatus.ACTIVE)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters =
                new GoalFilterDto(null, "programming", null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals("Learn programming basics", result.get(0).description());
    }

    @Test
    void getByFilters_shouldFilterByStatus() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Description 2")
                .status(GoalStatus.COMPLETED)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters =
                new GoalFilterDto(null, null, GoalStatus.ACTIVE, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals(GoalStatus.ACTIVE, result.get(0).status());
    }

    @Test
    void getByFilters_shouldFilterByMentorId() {
        Long requesterId = 1L;
        Long mentorId = 2L;
        User user = User.builder().id(requesterId).build();
        User mentor = User.builder().id(mentorId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Description 2")
                .status(GoalStatus.ACTIVE)
                .mentor(null)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto(null, null, null, mentorId);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals(mentorId, result.get(0).mentorId());
    }

    @Test
    void getByFilters_shouldApplyMultipleFilters() {
        Long requesterId = 1L;
        Long mentorId = 2L;
        User user = User.builder().id(requesterId).build();
        User mentor = User.builder().id(mentorId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Java Learning")
                .description("Learn Java programming")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Python Learning")
                .description("Learn Python programming")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("Java Advanced")
                .description("Advanced Java concepts")
                .status(GoalStatus.COMPLETED)
                .mentor(mentor)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2, goal3);
        GoalFilterDto filters =
                new GoalFilterDto("Java", "programming", GoalStatus.ACTIVE, mentorId);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals("Java Learning", result.get(0).title());
        assertEquals(GoalStatus.ACTIVE, result.get(0).status());
        assertEquals(mentorId, result.get(0).mentorId());
    }

    @Test
    void getByFilters_shouldReturnEmptyList_whenNoMatchesFound() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .status(GoalStatus.ACTIVE)
                .build();
        List<Goal> allGoals = List.of(goal1);
        GoalFilterDto filters =
                new GoalFilterDto("NonExistent", null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(0, result.size());
    }

    @Test
    void getByFilters_shouldHandlePartialMatches() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Java Programming Course")
                .description("Learn Java")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("JavaScript Course")
                .description("Learn JavaScript")
                .status(GoalStatus.ACTIVE)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto("Java", null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(2, result.size());
        assertEquals("Java Programming Course", result.get(0).title());
    }

    @Test
    void getByFilters_shouldHandleCaseSensitiveMatches() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Java Learning")
                .description("Learn Java")
                .status(GoalStatus.ACTIVE)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("python learning")
                .description("Learn Python")
                .status(GoalStatus.ACTIVE)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto("java", null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
    }

    @Test
    void getByFilters_shouldFilterByMentorId_whenMentorIsNull() {
        Long requesterId = 1L;
        Long mentorId = 2L;
        User user = User.builder().id(requesterId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Goal 1")
                .description("Description 1")
                .status(GoalStatus.ACTIVE)
                .mentor(null)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Goal 2")
                .description("Description 2")
                .status(GoalStatus.ACTIVE)
                .mentor(null)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2);
        GoalFilterDto filters = new GoalFilterDto(null, null, null, mentorId);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(0, result.size());
    }

    @Test
    void getByFilters_shouldThrowException_whenUserNotFound() {
        Long requesterId = 1L;
        GoalFilterDto filters = new GoalFilterDto(null, null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "User 1 not found",
                () -> goalService.getByFilters(filters)
        );
    }

    @Test
    void getByFilters_shouldHandleEmptyGoalList() {
        Long requesterId = 1L;
        User user = User.builder().id(requesterId).build();
        List<Goal> emptyGoals = List.of();
        GoalFilterDto filters = new GoalFilterDto(null, null, null, null);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(emptyGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(0, result.size());
    }

    @Test
    void getByFilters_shouldFilterByAllFields() {
        Long requesterId = 1L;
        Long mentorId = 2L;
        User user = User.builder().id(requesterId).build();
        User mentor = User.builder().id(mentorId).build();
        Goal goal1 = Goal.builder()
                .id(1L)
                .title("Java Learning")
                .description("Learn Java programming")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        Goal goal2 = Goal.builder()
                .id(2L)
                .title("Java Advanced")
                .description("Advanced Java programming")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        Goal goal3 = Goal.builder()
                .id(3L)
                .title("Python Learning")
                .description("Learn Python programming")
                .status(GoalStatus.ACTIVE)
                .mentor(mentor)
                .build();
        List<Goal> allGoals = List.of(goal1, goal2, goal3);
        GoalFilterDto filters =
                new GoalFilterDto("Java", "programming", GoalStatus.ACTIVE, mentorId);

        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.findById(requesterId)).thenReturn(java.util.Optional.of(user));
        when(goalRepository.findAll()).thenReturn(allGoals);

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(2, result.size());
        assertEquals("Java Learning", result.get(0).title());
        assertEquals("Java Advanced", result.get(1).title());
    }

    @Test
    void update_shouldThrowException_whenGoalIsCompleted() {
        final long goalId = 1001L;
        final long requesterId = 9001L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.COMPLETED)
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto =
                new UpdateGoalDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.COMPLETED);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Cannot update - Goal is already completed",
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldSucceed_whenSingleParticipantUserAndValidData() {
        final long goalId = 1002L;
        final long requesterId = 9002L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .title("old")
                .description("old")
                .deadline(LocalDateTime.now().plusDays(10))
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        LocalDateTime newDeadline = LocalDateTime.now().plusDays(5);
        UpdateGoalDto dto =
                new UpdateGoalDto("new title", "new desc", newDeadline, null, GoalStatus.ACTIVE);

        GoalDto result = goalService.update(goalId, dto);
        assertNotNull(result);

        assertEquals("new title", existing.getTitle());
        assertEquals("new desc", existing.getDescription());
        assertEquals(newDeadline, existing.getDeadline());
        assertEquals(GoalStatus.ACTIVE, existing.getStatus());


        assertEquals("new title", result.title());
        assertEquals("new desc", result.description());
        assertEquals(newDeadline, result.deadline());
        assertEquals(GoalStatus.ACTIVE, result.status());

        verify(goalRepository).save(existing);
    }

    @Test
    void update_shouldThrowException_whenRequesterIsNotOwner_inUserFlow() {
        final long goalId = 1003L;
        final long requesterId = 9003L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(111L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto = new UpdateGoalDto("t",
                "d",
                LocalDateTime.now().plusDays(2),
                null, GoalStatus.ACTIVE);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "RequesterId %s and userIds first id %s must be the same".formatted(requesterId, 111L),
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldThrowException_whenMultipleParticipants_inUserFlow() {
        final long goalId = 1004L;
        final long requesterId = 9004L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(requesterId).build(), User.builder().id(2L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto = new UpdateGoalDto("t",
                "d",
                LocalDateTime.now().plusDays(2),
                null, GoalStatus.ACTIVE);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "User cannot update goal if there are other participants",
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldThrowException_whenDeadlineInvalid() {
        final long goalId = 1005L;
        final long requesterId = 9005L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        LocalDateTime invalidDeadline = LocalDateTime.now().plusHours(12);
        UpdateGoalDto dto =
                new UpdateGoalDto("t", "d", invalidDeadline, null, GoalStatus.ACTIVE);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Deadline date should provide at least 1 day for achievement",
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldThrowException_whenMentorFlow_andMentorIdMissing() {
        final long goalId = 1006L;
        final long requesterMentorId = 9006L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(requesterMentorId).build())
                .users(List.of(User.builder().id(1L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto = new UpdateGoalDto(
                "t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE
        );

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "MentorId required",
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldThrowException_whenMentorFlow_andIdsMismatch() {
        final long goalId = 1007L;
        final long requesterMentorId = 9007L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(requesterMentorId).build())
                .users(List.of(User.builder().id(1L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto = new UpdateGoalDto(
                "t", "d", LocalDateTime.now().plusDays(2), 111L, GoalStatus.ACTIVE
        );

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Mentor %s does not belong to goal".formatted(requesterMentorId),
                () -> goalService.update(goalId, dto)
        );
    }

    @Test
    void update_shouldSucceed_whenMentorFlow_andIdsMatch() {
        final long goalId = 1008L;
        final long requesterMentorId = 9008L;

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .title("old")
                .description("old")
                .deadline(LocalDateTime.now().plusDays(10))
                .mentor(User.builder().id(requesterMentorId).build())
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        LocalDateTime newDeadline = LocalDateTime.now().plusDays(6);
        UpdateGoalDto dto = new UpdateGoalDto(
                "new t", "new d", newDeadline, requesterMentorId, GoalStatus.ACTIVE
        );

        GoalDto result = goalService.update(goalId, dto);
        assertNotNull(result);

        assertEquals("new t", existing.getTitle());
        assertEquals("new d", existing.getDescription());
        assertEquals(newDeadline, existing.getDeadline());
        assertEquals(GoalStatus.ACTIVE, existing.getStatus());

        assertEquals("new t", result.title());
        assertEquals("new d", result.description());
        assertEquals(newDeadline, result.deadline());
        assertEquals(GoalStatus.ACTIVE, result.status());

        verify(goalRepository).save(existing);
    }

    @Test
    void update_shouldKeepCreatedAtUnchanged() {
        final long goalId = 1009L;
        final long requesterId = 9009L;
        LocalDateTime created = LocalDateTime.now().minusDays(3);

        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .createdAt(created)
                .title("old")
                .description("old")
                .deadline(LocalDateTime.now().plusDays(10))
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        UpdateGoalDto dto = new UpdateGoalDto(
                "new", "new", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE
        );

        goalService.update(goalId, dto);

        assertEquals(created, existing.getCreatedAt());
    }

    private static class GoalMapperImplSpy implements GoalMapper {

        @Override
        public Goal toGoal(CreateGoalDto createGoalDto) {
            if (createGoalDto == null) {
                return null;
            }

            Goal.GoalBuilder goal = Goal.builder();

            goal.title(createGoalDto.title());
            goal.description(createGoalDto.description());
            goal.deadline(createGoalDto.deadline());

            return goal.build();
        }

        @Override
        public GoalDto toGoalDto(Goal goal) {
            if (goal == null) {
                return null;
            }

            return new GoalDto(
                    goal.getId(),
                    goal.getCreatedAt(),
                    goal.getUpdatedAt(),
                    goal.getTitle(),
                    goal.getDescription(),
                    goal.getDeadline(),
                    mapMentorToId(goal.getMentor()),
                    mapUsersToUserIds(goal.getUsers()),
                    goal.getStatus(),
                    mapParentGoalToId(goal.getParent())
            );
        }

        @Override
        public void update(UpdateGoalDto dto, Goal entity) {
            if (dto == null) {
                return;
            }

            if (dto.title() != null) {
                entity.setTitle(dto.title());
            }
            if (dto.description() != null) {
                entity.setDescription(dto.description());
            }
            if (dto.status() != null) {
                entity.setStatus(dto.status());
            }
            if (dto.deadline() != null) {
                entity.setDeadline(dto.deadline());
            }
        }
    }
}