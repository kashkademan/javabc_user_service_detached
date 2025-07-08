package school.faang.user_service.util;

import school.faang.user_service.entity.goal.Goal;

/**
 * GoalUtil — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>*
 *
 * @author Myrza
 * @since 08.07.2025
 */
public class GoalUtil {
    public static boolean userIsGoalMember(long userId, Goal goal) {
        if (goal.getMentor() != null && goal.getMentor().getId() == userId) {
            return true;
        }

        if (goal.getUsers() == null) {
            return false;
        }

        return goal.getUsers()
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }

}
