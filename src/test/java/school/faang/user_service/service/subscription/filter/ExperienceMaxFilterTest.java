package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.ExperienceMaxFilter;

public class ExperienceMaxFilterTest {
    private final ExperienceMaxFilter filter = new ExperienceMaxFilter();

    @Test
    public void testIsApplicableWhenZero() {
        boolean result = filter.isApplicable(new UserDtoFilter("NN","947",0,0));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenOk() {
        boolean result = filter.isApplicable(new UserDtoFilter("NN","947",1,2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsersSuccess() {
        User user = new User();
        user.setExperience(5);
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN","947",1,10);
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertTrue(result);
    }
    @Test
    public void testFilterUsersFailure() {
        User user = new User();
        user.setExperience(10);
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN","947",6,2);
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertFalse(result);
    }
    
}
