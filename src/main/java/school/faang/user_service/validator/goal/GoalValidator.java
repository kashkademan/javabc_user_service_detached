package school.faang.user_service.validator.goal;

import org.springframework.util.ObjectUtils;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public class GoalValidator {

    public static void validateCreateGoalByUser(GoalCreateByUserDto goalCreateByUserDto) {
        validateDeadline(goalCreateByUserDto.deadline());
    }

    public static void validateCreateGoalByMentor(GoalCreateByMentorDto goalCreateByMentorDto) {
        validateDeadline(goalCreateByMentorDto.deadline());

        if (goalCreateByMentorDto.userIds().size() != new HashSet<>(goalCreateByMentorDto.userIds()).size()) {
            throw new DataValidationException(
                    "Duplicate user IDs are not allowed: %s".formatted(goalCreateByMentorDto.userIds())
            );
        }
    }

    public static void validateUpdateGoal(GoalUpdateDto goalUpdateDto, Long requesterId, Goal goalToUpdate) {
        if (ObjectUtils.nullSafeEquals(goalToUpdate.getStatus(), GoalStatus.COMPLETED)) {
            throw new DataValidationException("Cannot update - Goal is already completed");
        }

        validateDeadline(goalUpdateDto.deadline());

        User mentor = goalToUpdate.getMentor();

        if (ObjectUtils.isEmpty(mentor)) {
            List<User> users = goalToUpdate.getUsers();

            if (users.size() > 1) {
                throw new DataValidationException("User cannot update goal if there are other participants");
            }

            if (!ObjectUtils.nullSafeEquals(requesterId, users.get(0).getId())) {
                throw new ForbiddenException(
                        "RequesterId %s and userIds first id %s must be the same"
                                .formatted(requesterId, users.get(0).getId())
                );
            }
        } else {
            if (ObjectUtils.isEmpty(goalUpdateDto.mentorId())) {
                throw new ForbiddenException("MentorId required");
            }

            if (!requesterId.equals(goalUpdateDto.mentorId()) || !requesterId.equals(mentor.getId())) {
                throw new ForbiddenException("Mentor %s does not belong to goal".formatted(requesterId));
            }
        }
    }

    private static void validateDeadline(LocalDateTime deadline) {
        if (!ObjectUtils.isEmpty(deadline) && deadline.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new DataValidationException("Goal requires minimum 1 day to achieve");
        }
    }
}
