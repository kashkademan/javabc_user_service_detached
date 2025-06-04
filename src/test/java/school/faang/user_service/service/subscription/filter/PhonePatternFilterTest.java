package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.PhonePatternFilter;

public class PhonePatternFilterTest {
    private final PhonePatternFilter filter = new PhonePatternFilter();

    @Test
    public void testIsApplicable_PhoneNull() {
        boolean result = filter.isApplicable(new UserDtoFilter());
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicable_PhoneBlank() {
        boolean result = filter.isApplicable(new UserDtoFilter("nn", " ", 1, 2));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicable_PhoneOk() {
        boolean result = filter.isApplicable(new UserDtoFilter("nn", "111", 1, 2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsers_Success() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("nn", "947", 1, 2);
        User user = new User();
        user.setPhone("947");
        boolean result = filter.filterUsers(user, userDtoFilter);
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsers_Failure() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("nn", "948", 1, 2);
        User user = new User();
        user.setPhone("947");
        boolean result = filter.filterUsers(user, userDtoFilter);
        Assertions.assertFalse(result);
    }
}
