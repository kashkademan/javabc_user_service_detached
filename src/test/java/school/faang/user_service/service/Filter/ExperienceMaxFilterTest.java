package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.ExperienceMax;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class ExperienceMaxFilterTest {
    private final ExperienceMax experienceMax = new ExperienceMax();

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null,
                null, 27);
        boolean result = experienceMax.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null,
                null, null);
        boolean result = experienceMax.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = Stream.of(
                User.builder().experience(27).build(),
                User.builder().experience(40).build()
        );

        Stream<User> user = experienceMax.apply(users, new UserFilterDto(null, null,
                null, 30));

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

        Stream<User> user = experienceMax.apply(users, new UserFilterDto(null, null,
                null, 25));

        List<User> userList = user.toList();
        assertEquals(0, userList.size());
    }
}
