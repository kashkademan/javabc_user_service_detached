package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.NamePatternFilter;

public class NamePatternFilterTest {
    private final NamePatternFilter namePatternFilter = new NamePatternFilter();

    @Test
    public void testIsApplicable_WhenNull() {
        boolean result = namePatternFilter.isApplicable(new UserDtoFilter());
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicable_WhenNameBlank() {
        boolean result = namePatternFilter.isApplicable(new UserDtoFilter("", " ", 1, 2));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicable_WhenCorrectParams() {
        boolean result = namePatternFilter.isApplicable(new UserDtoFilter("NN", "947", 1, 2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsers_Success() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN", "947", 1, 2);
        User user = new User();
        user.setAboutMe("NN");
        boolean result = namePatternFilter.filterUsers(user, userDtoFilter);
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsers_Failure() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("M", "947", 1, 2);
        User user = new User();
        user.setAboutMe("NN");
        boolean result = namePatternFilter.filterUsers(user, userDtoFilter);
        Assertions.assertFalse(result);
    }

}
