package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GoalUpdateBeforeFilterTest {
    private final GoalUpdateBeforeFilter filter = new GoalUpdateBeforeFilter();

    @Test
    void doFilterTrue() {
        Goal goal = new Goal();
        goal.setUpdatedAt(LocalDateTime.of(2023, 1, 1, 0, 0));

        GoalFilterDto dto = new GoalFilterDto();
        dto.setUpdatedBefore(LocalDateTime.of(2023, 12, 31, 0, 0));

        assertTrue(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterFalse() {
        Goal goal = new Goal();
        goal.setUpdatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        GoalFilterDto dto = new GoalFilterDto();
        dto.setUpdatedBefore(LocalDateTime.of(2023, 12, 31, 0, 0));

        assertFalse(filter.doFilter(goal, dto));
    }

    @Test
    void isApplicableTrue() {
        GoalFilterDto dto = new GoalFilterDto();
        dto.setUpdatedBefore(LocalDateTime.now());

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void isApplicableFalse() {
        GoalFilterDto dto = new GoalFilterDto();

        assertFalse(filter.isApplicable(dto));
    }
}
