package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import static org.junit.jupiter.api.Assertions.*;

class GoalDescriptionFilterTest {
    private final GoalDescriptionFilter filter = new GoalDescriptionFilter();

    @Test
    void doFilterTrue() {
        Goal goal = new Goal();
        goal.setDescription("Learn Java and Spring Boot");

        GoalFilterDto dto = new GoalFilterDto();
        dto.setDescription("Java");

        assertTrue(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterFalse() {
        Goal goal = new Goal();
        goal.setDescription("Learn Python");

        GoalFilterDto dto = new GoalFilterDto();
        dto.setDescription("Java");

        assertFalse(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterGoalDescriptionNull() {
        Goal goal = new Goal();
        goal.setDescription(null);

        GoalFilterDto dto = new GoalFilterDto();
        dto.setDescription("Java");

        assertThrows(NullPointerException.class, () -> filter.doFilter(goal, dto));
    }

    @Test
    void isApplicableTrue() {
        GoalFilterDto dto = new GoalFilterDto();
        dto.setDescription("Anything");

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void isApplicableFalse() {
        GoalFilterDto dto = new GoalFilterDto();

        assertFalse(filter.isApplicable(dto));
    }
}
