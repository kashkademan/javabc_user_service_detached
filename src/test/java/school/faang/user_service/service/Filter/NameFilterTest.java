package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.NameFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class NameFilterTest {
    private final NameFilter nameFilter = new NameFilter();

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = new UserFilterDto("Alex", null, null, null);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = new UserFilterDto(null, null, null, null);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsEmpty() {
        UserFilterDto userFilterDto = new UserFilterDto("", null, null, null);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsBlank() {
        UserFilterDto userFilterDto = new UserFilterDto("   ", null, null, null);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = Stream.of(
                User.builder().username("Alex").build(),
                User.builder().username("Fred").build()
        );

        Stream<User> user = nameFilter.apply(users, new UserFilterDto("Alex", null,
                null, null));

        List<User> userList = user.toList();
        assertEquals(1, userList.size());
        assertEquals("Alex", userList.get(0).getUsername());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = Stream.of(
                User.builder().username("Alex").build(),
                User.builder().username("Fred").build()
        );

        Stream<User> user = nameFilter.apply(users, new UserFilterDto("John", null,
                null, null));

        List<User> userList = user.toList();
        assertEquals(0, userList.size());
    }
}
