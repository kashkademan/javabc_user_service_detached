package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperienceFilterTest {

    private final ExperienceFilter experienceFilter = new ExperienceFilter();

    @Test
    public void testIsApplicable_WhenMinExperienceSetReturnsTrue() {
        boolean result = experienceFilter.isApplicable(new UserFiltersDto(null, null, 1, Integer.MAX_VALUE));
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WhenMaxExperienceSetReturnsTrue() {
        boolean result = experienceFilter.isApplicable(new UserFiltersDto(null, null, 0, 10));
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WhenBothMinAndMaxExperienceSetReturnsTrue() {
        boolean result = experienceFilter.isApplicable(new UserFiltersDto(null, null, 1, 8));
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WhenDefaultValuesReturnsFalse() {
        boolean result = experienceFilter.isApplicable(new UserFiltersDto(null, null, 0, Integer.MAX_VALUE));
        assertFalse(result);
    }

    @Test
    public void testApply_WhenMinExperienceFilterReturnsSelectedUsers() {
        Stream<User> users = Stream.of(
                User.builder().experience(1).build(),
                User.builder().experience(5).build(),
                User.builder().experience(3).build()
        );
        Stream<User> result = experienceFilter.apply(users, new UserFiltersDto(null, null, 3, Integer.MAX_VALUE));
        List<User> userList = result.toList();
        assertEquals(2, userList.size());
        assertTrue(userList.get(0).getExperience() >= 3);
        assertTrue(userList.get(1).getExperience() >= 3);
    }

    @Test
    public void testApply_WhenMaxExperienceFilterReturnsSelectedUsers() {
        Stream<User> users = Stream.of(
                User.builder().experience(3).build(),
                User.builder().experience(10).build(),
                User.builder().experience(5).build()
        );
        Stream<User> result = experienceFilter.apply(users, new UserFiltersDto(null, null, 0, 5));
        List<User> userList = result.toList();
        assertEquals(2, userList.size());
        assertTrue(userList.get(0).getExperience() <= 5);
        assertTrue(userList.get(1).getExperience() <= 5);
    }

    @Test
    public void testApply_WhenNoUsersMatchReturnsEmptyStream() {
        Stream<User> users = Stream.of(
                User.builder().experience(2).build(),
                User.builder().experience(3).build()
        );
        Stream<User> result = experienceFilter.apply(users, new UserFiltersDto(null, null, 5, 10));
        List<User> userList = result.toList();
        assertEquals(0, userList.size());
    }
}
