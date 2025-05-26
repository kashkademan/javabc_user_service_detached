package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import static org.junit.jupiter.api.Assertions.*;

class GoalTitleFilterTest {
    private final GoalTitleFilter filter = new GoalTitleFilter();

    @Test
    void doFilterFalse() {
        Goal goal = new Goal();
        goal.setTitle("Goal Title");
        GoalFilterDto criteria = new GoalFilterDto();
        criteria.setTitle("anyTitle");

        assertFalse(filter.doFilter(goal, criteria));
    }

    @Test
    void doFilterTrue() {
        Goal goal = new Goal();
        String goal_title = "Goal Title";
        goal.setTitle(goal_title);
        GoalFilterDto criteria = new GoalFilterDto();
        criteria.setTitle(goal_title);

        assertTrue(filter.doFilter(goal, criteria));
    }

    @Test
    void isApplicableTrue() {
        GoalFilterDto criteria = new GoalFilterDto();
        criteria.setTitle("anyTitle");

        assertTrue(filter.isApplicable(criteria));
    }

    @Test
    void isApplicableFalse() {
        GoalFilterDto criteria = new GoalFilterDto();

        assertNull(criteria.getTitle());
        assertFalse(filter.isApplicable(criteria));
    }

}