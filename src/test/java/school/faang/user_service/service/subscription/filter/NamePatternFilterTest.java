package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.NamePatternFilter;

public class NamePatternFilterTest {
    private final NamePatternFilter namePatternFilter = new NamePatternFilter();

    @Test
    public void testIsApplicableWhenNull() {
       boolean result = namePatternFilter.isApplicable(new UserDtoFilter());
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameBlank() {
        boolean result = namePatternFilter.isApplicable(new UserDtoFilter(""," ",1,2));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenOk() {
        boolean result = namePatternFilter.isApplicable(new UserDtoFilter("NN","947",1,2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsersSuccess() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN","947",1,2);
        User user = new User();
        user.setAboutMe("NN");
        boolean result = namePatternFilter.filterUsers(user,userDtoFilter);
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsersFailure() {
        UserDtoFilter userDtoFilter = new UserDtoFilter("M","947",1,2);
        User user = new User();
        user.setAboutMe("NN");
        boolean result = namePatternFilter.filterUsers(user,userDtoFilter);
        Assertions.assertFalse(result);
    }

}
