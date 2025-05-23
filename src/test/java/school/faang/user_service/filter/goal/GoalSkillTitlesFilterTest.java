package school.faang.user_service.filter.goal;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoalSkillTitlesFilterTest {
    private final GoalSkillTitlesFilter filter = new GoalSkillTitlesFilter();

    @Test
    void doFilterTrue() {
        Skill skill1 = new Skill();
        skill1.setTitle("Java");
        Skill skill2 = new Skill();
        skill2.setTitle("Kotlin");

        Goal goal = new Goal();
        goal.setSkillsToAchieve(List.of(skill1, skill2));

        GoalFilterDto dto = new GoalFilterDto();
        dto.setSkillTitles(List.of("Java"));

        assertTrue(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterFalse() {
        Skill skill = new Skill();
        skill.setTitle("Python");

        Goal goal = new Goal();
        goal.setSkillsToAchieve(List.of(skill));

        GoalFilterDto dto = new GoalFilterDto();
        dto.setSkillTitles(List.of("Java"));

        assertFalse(filter.doFilter(goal, dto));
    }

    @Test
    void doFilterEmptySkillsReturnsTrue() {
        Goal goal = new Goal();
        goal.setSkillsToAchieve(List.of());

        GoalFilterDto dto = new GoalFilterDto();
        dto.setSkillTitles(List.of());

        assertTrue(filter.doFilter(goal, dto));
    }

    @Test
    void isApplicableTrue() {
        GoalFilterDto dto = new GoalFilterDto();
        dto.setSkillTitles(List.of("Java"));

        assertTrue(filter.isApplicable(dto));
    }

    @Test
    void isApplicableFalse() {
        GoalFilterDto dto = new GoalFilterDto();

        assertFalse(filter.isApplicable(dto));
    }
}
