package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.PhoneFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneFilterTest {
    private final PhoneFilter phoneFilter = new PhoneFilter();

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = new UserFilterDto(null, "89043354392", null, null);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null, null, null);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsEmpty() {
        UserFilterDto userFilterDto = new UserFilterDto(null, "", null, null);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsBlank() {
        UserFilterDto userFilterDto = new UserFilterDto(null, "   ", null, null);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = Stream.of(
                User.builder().phone("89230462456").build(),
                User.builder().phone("89037572905").build()
        );

        Stream<User> user = phoneFilter.apply(users, new UserFilterDto(null, "89230462456",
                null, null));

        List<User> userList = user.toList();
        assertEquals(1, userList.size());
        assertEquals("89230462456", userList.get(0).getPhone());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = Stream.of(
                User.builder().username("89230462456").build(),
                User.builder().username("89037572905").build()
        );

        Stream<User> user = phoneFilter.apply(users, new UserFilterDto(null, "89037572937",
                null, null));

        List<User> userList = user.toList();
        assertEquals(0, userList.size());
    }
}
