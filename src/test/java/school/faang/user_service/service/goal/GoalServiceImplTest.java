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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
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
    private GoalServiceImpl goalService;
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

        // DTO assertions
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
        when(userRepository.findById(requesterMentorId)).thenReturn(
                Optional.of(User.builder().id(requesterMentorId).build())
        );
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

        // DTO assertions
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

        // DTO assertions
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
        when(userRepository.findById(requesterMentorId)).thenReturn(
                Optional.of(User.builder().id(requesterMentorId).build())
        );
        when(userRepository.findById(userId1)).thenReturn(Optional.of(User.builder().id(userId1).build()));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(User.builder().id(userId2).build()));
        when(skillRepository.findById(skillId1)).thenReturn(Optional.of(Skill.builder().id(skillId1).build()));
        when(skillRepository.findById(skillId2)).thenReturn(Optional.of(Skill.builder().id(skillId2).build()));

        GoalDto result = goalService.create(createGoalDto);

        // DTO assertions
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
    }
}