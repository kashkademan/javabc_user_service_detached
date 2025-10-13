package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
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
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);
    @Mock
    private UserContext userContext;
    @InjectMocks
    private GoalService goalService;

    @Captor
    private ArgumentCaptor<Goal> goalCaptor;
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> guaranteesCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(goalService, "maxGoalsPerUser", 2);
    }

    @Test
    void createByUser_shouldThrow_whenUserHasMaxActiveGoals() {
        long requesterId = 1L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(2);

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "t", "d", LocalDateTime.now().plusDays(2), null, null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "User has 2 active goals",
                () -> goalService.createByUser(dto)
        );
    }

    @Test
    void createByUser_shouldSave_whenNoSkillsAndNoParent() {
        long requesterId = 10L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "title", "desc", LocalDateTime.now().plusDays(2), null, null
        );

        GoalDto result = goalService.createByUser(dto);

        assertEquals(GoalStatus.ACTIVE, result.status());
        assertEquals(1, result.userIds().size());
        assertEquals(requesterId, result.userIds().get(0));

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();

        assertNotNull(saved);
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNull(saved.getMentor());
        assertTrue(saved.getUsers() != null && saved.getUsers().size() == 1);
        assertEquals(requesterId, saved.getUsers().get(0).getId());
        assertTrue(saved.getSkillsToAchieve() == null || saved.getSkillsToAchieve().isEmpty());
        assertNull(saved.getParent());
    }

    @Test
    void createByUser_shouldSave_withParentAndSkills() {
        long requesterId = 11L;
        long parentId = 100L;
        long skillId1 = 3L;
        long skillId2 = 4L;

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());
        when(goalRepository.getByIdOrThrow(parentId)).thenReturn(Goal.builder().id(parentId).build());
        when(skillRepository.findAllById(List.of(skillId1, skillId2)))
                .thenReturn(List.of(Skill.builder().id(skillId1).build(), Skill.builder().id(skillId2).build()));

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "title", "desc", LocalDateTime.now().plusDays(3), List.of(skillId1, skillId2), parentId
        );

        GoalDto result = goalService.createByUser(dto);

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();
        assertNotNull(saved.getParent());
        assertEquals(parentId, saved.getParent().getId());
        assertEquals(2, saved.getSkillsToAchieve().size());
        assertEquals(skillId1, saved.getSkillsToAchieve().get(0).getId());
        assertEquals(skillId2, saved.getSkillsToAchieve().get(1).getId());

        
    }

    @Test
    void createByUser_shouldThrow_whenSkillsDuplicate() {
        long requesterId = 12L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(7L, 7L), null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Skill IDs must contain only unique values: [7, 7]",
                () -> goalService.createByUser(dto)
        );
    }

    @Test
    void createByUser_shouldThrow_whenSomeSkillsMissing() {
        long requesterId = 13L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());
        when(skillRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(Skill.builder().id(1L).build()));

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(1L, 2L), null
        );

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Skills not found by ids: [2]",
                () -> goalService.createByUser(dto)
        );
    }

    @Test
    void createByUser_shouldThrow_whenParentMissing() {
        long requesterId = 14L;
        long missingParentId = 404L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.countActiveGoalsPerUser(requesterId)).thenReturn(0);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());
        when(goalRepository.getByIdOrThrow(missingParentId))
                .thenThrow(new EntityNotFoundException("Goal %d not found".formatted(missingParentId)));

        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "t", "d", LocalDateTime.now().plusDays(2), null, missingParentId
        );

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Goal %d not found".formatted(missingParentId),
                () -> goalService.createByUser(dto)
        );
    }

    @Test
    void createByMentor_shouldThrow_whenUserExceedsActiveGoals() {
        long requesterMentorId = 20L;
        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(userRepository.getByIdOrThrow(requesterMentorId)).thenReturn(User.builder().id(requesterMentorId).build());
        when(goalRepository.countActiveGoalsPerUser(21L)).thenReturn(2);

        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(21L), null, null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "User has 2 active goals",
                () -> goalService.createByMentor(dto)
        );
    }

    @Test
    void createByMentor_shouldThrow_whenSomeUsersMissing() {
        long requesterMentorId = 21L;
        List<Long> userIds = List.of(1L, 2L);
        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(userRepository.getByIdOrThrow(requesterMentorId)).thenReturn(User.builder().id(requesterMentorId).build());
        when(goalRepository.countActiveGoalsPerUser(1L)).thenReturn(0);
        when(goalRepository.countActiveGoalsPerUser(2L)).thenReturn(0);
        when(userRepository.findAllById(userIds)).thenReturn(List.of(User.builder().id(1L).build()));

        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(2), userIds, null, null
        );

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "Users not found by userIds: [2]",
                () -> goalService.createByMentor(dto)
        );
    }

    @Test
    void createByMentor_shouldSaveAndCreateGuarantees_whenSkillsPresent() {
        long requesterMentorId = 22L;
        long userId1 = 31L;
        long userId2 = 32L;
        long skillId1 = 41L;
        long skillId2 = 42L;

        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(userRepository.getByIdOrThrow(requesterMentorId)).thenReturn(User.builder().id(requesterMentorId).build());
        when(goalRepository.countActiveGoalsPerUser(userId1)).thenReturn(0);
        when(goalRepository.countActiveGoalsPerUser(userId2)).thenReturn(0);
        when(userRepository.findAllById(List.of(userId1, userId2)))
                .thenReturn(List.of(User.builder().id(userId1).build(), User.builder().id(userId2).build()));
        when(skillRepository.findAllById(List.of(skillId1, skillId2)))
                .thenReturn(List.of(Skill.builder().id(skillId1).build(), Skill.builder().id(skillId2).build()));

        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(3), List.of(userId1, userId2), List.of(skillId1, skillId2), null
        );

        GoalDto result = goalService.createByMentor(dto);

        assertEquals(GoalStatus.ACTIVE, result.status());
        assertEquals(2, result.userIds().size());
        assertEquals(requesterMentorId, result.mentorId());

        assertEquals(GoalStatus.ACTIVE, result.status());
        assertEquals(requesterMentorId, result.mentorId());
        assertEquals(2, result.userIds().size());

        verify(userSkillGuaranteeRepository).saveAll(guaranteesCaptor.capture());
        List<UserSkillGuarantee> guarantees = guaranteesCaptor.getValue();
        assertNotNull(guarantees);
        assertEquals(4, guarantees.size());

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertNotNull(saved.getMentor());
        assertEquals(requesterMentorId, saved.getMentor().getId());
        assertEquals(2, saved.getUsers().size());
        assertEquals(2, saved.getSkillsToAchieve().size());
    }

    @Test
    void createByMentor_shouldSave_whenNoSkills() {
        long requesterMentorId = 23L;
        long userId = 24L;
        when(userContext.getUserId()).thenReturn(requesterMentorId);
        when(userRepository.getByIdOrThrow(requesterMentorId)).thenReturn(User.builder().id(requesterMentorId).build());
        when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(0);
        when(userRepository.findAllById(List.of(userId))).thenReturn(List.of(User.builder().id(userId).build()));

        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(userId), null, null
        );

        GoalDto result = goalService.createByMentor(dto);

        verify(goalRepository).save(goalCaptor.capture());
        Goal saved = goalCaptor.getValue();
        assertEquals(GoalStatus.ACTIVE, saved.getStatus());
        assertTrue(saved.getSkillsToAchieve() == null || saved.getSkillsToAchieve().isEmpty());
        assertEquals(1, saved.getUsers().size());
        assertNotNull(saved.getMentor());
    }

    @Test
    void delete_shouldThrow_whenGoalIsParent_inUserFlow() {
        long requesterId = 30L;
        long goalId = 300L;
        Goal goal = Goal.builder().id(goalId).users(List.of(User.builder().id(requesterId).build())).build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.isParent(goalId)).thenReturn(true);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Goal %s is parent and cannot be delete".formatted(goalId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldThrow_whenRequesterNotParticipant_andNoMentor() {
        long requesterId = 31L;
        long goalId = 301L;
        Goal goal = Goal.builder().id(goalId).users(List.of(User.builder().id(999L).build())).build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.isParent(goalId)).thenReturn(false);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User %s cannot delete goal %s".formatted(requesterId, goalId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldDeleteById_whenNoMentor_andSingleParticipant() {
        long requesterId = 32L;
        long goalId = 302L;
        Goal goal = Goal.builder().id(goalId).users(List.of(User.builder()
                .id(requesterId).build())).skillsToAchieve(List.of()).build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.isParent(goalId)).thenReturn(false);

        goalService.delete(goalId);

        verify(goalRepository).deleteById(goalId);
        verifyNoInteractions(userSkillGuaranteeRepository);
    }

    @Test
    void delete_shouldRemoveUser_whenNoMentor_andMultipleParticipants() {
        long requesterId = 33L;
        long goalId = 303L;
        Goal goal = Goal.builder()
                .id(goalId)
                .users(List.of(User.builder().id(requesterId).build(), User.builder().id(777L).build()))
                .skillsToAchieve(List.of())
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);
        when(goalRepository.isParent(goalId)).thenReturn(false);

        goalService.delete(goalId);

        verify(goalRepository).deleteUserFromGoal(requesterId, goalId);
        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_shouldThrow_whenRequesterNotMentor_inMentorFlow() {
        long requesterId = 34L;
        long goalId = 304L;
        long mentorId = 1000L;
        Goal goal = Goal.builder()
                .id(goalId)
                .mentor(User.builder().id(mentorId).build())
                .users(List.of(User.builder().id(10L).build()))
                .skillsToAchieve(List.of())
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "MentorId %s and requesterId %s are different".formatted(mentorId, requesterId),
                () -> goalService.delete(goalId)
        );
    }

    @Test
    void delete_shouldDeleteAndRemoveGuarantees_whenMentorFlow_withSkills() {
        long mentorId = 35L;
        long goalId = 305L;
        Skill s1 = Skill.builder().id(1L).build();
        Skill s2 = Skill.builder().id(2L).build();
        Goal goal = Goal.builder()
                .id(goalId)
                .mentor(User.builder().id(mentorId).build())
                .users(List.of(User.builder().id(10L).build()))
                .skillsToAchieve(List.of(s1, s2))
                .build();

        when(userContext.getUserId()).thenReturn(mentorId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(goal);

        goalService.delete(goalId);

        verify(goalRepository).deleteById(goalId);
        verify(userSkillGuaranteeRepository).deleteBySkillIdIn(List.of(1L, 2L));
    }

    @Test
    void getByFilters_shouldThrow_whenRequesterMissing() {
        long requesterId = 40L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId))
                .thenThrow(new EntityNotFoundException("User %d not found".formatted(requesterId)));

        GoalFilterDto filters = new GoalFilterDto(null, null, null, null);

        TestUtils.assertThrowsWithMessage(
                EntityNotFoundException.class,
                "User %d not found".formatted(requesterId),
                () -> goalService.getByFilters(filters)
        );
    }

    @Test
    void getByFilters_shouldApplyAllFilters() {
        long requesterId = 41L;
        when(userContext.getUserId()).thenReturn(requesterId);
        when(userRepository.getByIdOrThrow(requesterId)).thenReturn(User.builder().id(requesterId).build());

        User mentor = User.builder().id(5L).build();
        Goal g1 = Goal.builder().id(1L).title("Java Basics").description("Learn Java")
                .status(GoalStatus.ACTIVE).mentor(mentor).build();
        Goal g2 = Goal.builder().id(2L).title("Python").description("Data")
                .status(GoalStatus.COMPLETED).mentor(mentor).build();
        Goal g3 = Goal.builder().id(3L).title("Java Advanced").description("Deep Java programming")
                .status(GoalStatus.ACTIVE).mentor(mentor).build();

        when(goalRepository.findAll()).thenReturn(List.of(g1, g2, g3));

        GoalFilterDto filters = new GoalFilterDto("Java", "programming", GoalStatus.ACTIVE, mentor.getId());

        List<GoalDto> result = goalService.getByFilters(filters);

        assertEquals(1, result.size());
        assertEquals("Java Advanced", result.get(0).title());
        assertEquals(GoalStatus.ACTIVE, result.get(0).status());
        assertEquals(mentor.getId(), result.get(0).mentorId());
    }

    @Test
    void update_shouldApplyChanges_andSave() {
        long requesterId = 50L;
        long goalId = 500L;
        Goal existing = Goal.builder()
                .id(goalId)
                .status(GoalStatus.ACTIVE)
                .title("old t")
                .description("old d")
                .deadline(LocalDateTime.now().plusDays(10))
                .users(List.of(User.builder().id(requesterId).build()))
                .build();

        when(userContext.getUserId()).thenReturn(requesterId);
        when(goalRepository.getByIdOrThrow(goalId)).thenReturn(existing);

        LocalDateTime newDeadline = LocalDateTime.now().plusDays(3);
        GoalUpdateDto dto = new GoalUpdateDto("new t", "new d", newDeadline, null, GoalStatus.ACTIVE);

        GoalDto result = goalService.update(goalId, dto);

        assertNotNull(result);
        assertEquals("new t", result.title());
        assertEquals("new d", result.description());
        assertEquals(newDeadline, result.deadline());
        assertEquals(GoalStatus.ACTIVE, result.status());

        verify(goalRepository).save(existing);
        assertEquals("new t", existing.getTitle());
        assertEquals("new d", existing.getDescription());
        assertEquals(newDeadline, existing.getDeadline());
        assertEquals(GoalStatus.ACTIVE, existing.getStatus());
    }
}


