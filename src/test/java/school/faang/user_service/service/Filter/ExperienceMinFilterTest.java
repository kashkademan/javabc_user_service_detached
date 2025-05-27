package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.ExperienceMinFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperienceMinFilterTest {
    private final ExperienceMinFilter experienceMinFilter = new ExperienceMinFilter();

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null,
                20, null);
        boolean result = experienceMinFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null,
                null, null);
        boolean result = experienceMinFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = Stream.of(
                User.builder().experience(27).build(),
                User.builder().experience(13).build()
        );

        Stream<User> user = experienceMinFilter.apply(users, new UserFilterDto(null, null,
                20, null));

        List<User> userList = user.toList();
        assertEquals(1, userList.size());
        assertEquals(27, userList.get(0).getExperience());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = Stream.of(
                User.builder().experience(27).build(),
                User.builder().experience(40).build()
        );

        Stream<User> user = experienceMinFilter.apply(users, new UserFilterDto(null, null,
                50, null));

        List<User> userList = user.toList();
        assertEquals(0, userList.size());
    }
}
