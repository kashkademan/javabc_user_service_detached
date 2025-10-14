package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.helpers.TestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GoalValidatorTest {

    @ParameterizedTest
    @MethodSource("invalidDeadlines")
    void validateCreateGoalByUser_shouldThrow_whenDeadlineInvalid(LocalDateTime invalidDeadline) {
        GoalCreateByUserDto dto = new GoalCreateByUserDto(
                "t", "d", invalidDeadline, null, null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Goal requires minimum 1 day to achieve",
                () -> GoalValidator.validateCreateGoalByUser(dto)
        );
    }

    @Test
    void validateCreateGoalByUser_shouldPass_whenDeadlineNullOrValid() {
        GoalCreateByUserDto nullDeadline = new GoalCreateByUserDto(
                "t", "d", null, null, null
        );
        GoalCreateByUserDto validDeadline = new GoalCreateByUserDto(
                "t", "d", LocalDateTime.now().plusDays(2), null, null
        );

        assertDoesNotThrow(() -> GoalValidator.validateCreateGoalByUser(nullDeadline));
        assertDoesNotThrow(() -> GoalValidator.validateCreateGoalByUser(validDeadline));
    }

    @ParameterizedTest
    @MethodSource("invalidDeadlines")
    void validateCreateGoalByMentor_shouldThrow_whenDeadlineInvalid(LocalDateTime invalidDeadline) {
        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", invalidDeadline, List.of(1L), null, null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Goal requires minimum 1 day to achieve",
                () -> GoalValidator.validateCreateGoalByMentor(dto)
        );
    }

    @Test
    void validateCreateGoalByMentor_shouldThrow_whenUserIdsContainDuplicates() {
        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(1L, 2L, 2L), null, null
        );

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Duplicate user IDs are not allowed: [1, 2, 2]",
                () -> GoalValidator.validateCreateGoalByMentor(dto)
        );
    }

    @Test
    void validateCreateGoalByMentor_shouldPass_whenDeadlineValidAndUsersUnique() {
        GoalCreateByMentorDto dto = new GoalCreateByMentorDto(
                "t", "d", LocalDateTime.now().plusDays(2), List.of(1L, 2L), null, null
        );
        assertDoesNotThrow(() -> GoalValidator.validateCreateGoalByMentor(dto));
    }

    @Test
    void validateUpdateGoal_shouldThrow_whenGoalAlreadyCompleted() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder().status(GoalStatus.COMPLETED).build();

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Cannot update - Goal is already completed",
                () -> GoalValidator.validateUpdateGoal(dto, 1L, goal)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidDeadlines")
    void validateUpdateGoal_shouldThrow_whenDeadlineInvalid(LocalDateTime invalidDeadline) {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", invalidDeadline, null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder().status(GoalStatus.ACTIVE).users(List.of(User.builder().id(1L).build())).build();

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Goal requires minimum 1 day to achieve",
                () -> GoalValidator.validateUpdateGoal(dto, 1L, goal)
        );
    }

    @Test
    void validateUpdateGoal_shouldThrow_whenUserFlow_multipleParticipants() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "User cannot update goal if there are other participants",
                () -> GoalValidator.validateUpdateGoal(dto, 1L, goal)
        );
    }

    @Test
    void validateUpdateGoal_shouldThrow_whenUserFlow_ownerMismatch() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(999L).build()))
                .build();

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "RequesterId 1 and userIds first id 999 must be the same",
                () -> GoalValidator.validateUpdateGoal(dto, 1L, goal)
        );
    }

    @Test
    void validateUpdateGoal_shouldPass_whenUserFlow_singleOwnerMatches() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .users(List.of(User.builder().id(1L).build()))
                .build();

        assertDoesNotThrow(() -> GoalValidator.validateUpdateGoal(dto, 1L, goal));
    }

    @Test
    void validateUpdateGoal_shouldThrow_whenMentorFlow_mentorIdMissingInDto() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), null, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(10L).build())
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "MentorId required",
                () -> GoalValidator.validateUpdateGoal(dto, 10L, goal)
        );
    }

    @Test
    void validateUpdateGoal_shouldThrow_whenMentorFlow_requesterNotMentor() {
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), 999L, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(10L).build())
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Mentor 10 does not belong to goal",
                () -> GoalValidator.validateUpdateGoal(dto, 10L, goal)
        );
    }

    @Test
    void validateUpdateGoal_shouldPass_whenMentorFlow_requesterIsMentorAndIdsMatch() {
        long mentorId = 10L;
        GoalUpdateDto dto = new GoalUpdateDto("t", "d", LocalDateTime.now().plusDays(2), mentorId, GoalStatus.ACTIVE);
        Goal goal = Goal.builder()
                .status(GoalStatus.ACTIVE)
                .mentor(User.builder().id(mentorId).build())
                .users(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .build();

        assertDoesNotThrow(() -> GoalValidator.validateUpdateGoal(dto, mentorId, goal));
    }

    private static Stream<LocalDateTime> invalidDeadlines() {
        return Stream.of(
                LocalDateTime.now().plusDays(1).minusMinutes(1),
                LocalDateTime.now().plusDays(1).minusHours(1),
                LocalDateTime.now().plusDays(1).minusSeconds(1),
                LocalDateTime.now().plusHours(12)
        );
    }
}


