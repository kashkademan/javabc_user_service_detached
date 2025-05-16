package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import static org.junit.jupiter.api.Assertions.*;

class GoalStatusFilterTest {
    private final GoalStatusFilter filter = new GoalStatusFilter();

    @Test
    void doFilterTrue() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        GoalFilterDto dto = new GoalFilterDto();
        dto.setStatus(GoalStatus.ACTIVE);

        assertTrue(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterFalse() {
        Goal goal = new Goal();
        goal.setStatus(GoalStatus.ACTIVE);
        GoalFilterDto dto = new GoalFilterDto();
        dto.setStatus(GoalStatus.COMPLETED);

        assertFalse(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterNullStatus() {
        Goal goal = new Goal();
        goal.setStatus(null);
        GoalFilterDto dto = new GoalFilterDto();
        dto.setStatus(GoalStatus.ACTIVE);

        assertFalse(filter.doFilter(goal, dto));
    }

    @Test
    void isApplicableTrue() {
        GoalFilterDto dto = new GoalFilterDto();
        dto.setStatus(GoalStatus.ACTIVE);

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void isApplicableFalse() {
        GoalFilterDto dto = new GoalFilterDto();

        assertFalse(filter.isApplicable(dto));
    }
}
