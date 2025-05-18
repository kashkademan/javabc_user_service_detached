package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.ExperienceMinFilter;

public class ExperienceMinFilterTest {
    private final ExperienceMinFilter filter = new ExperienceMinFilter();

    @Test
    public void testIsApplicable_WhenZero() {
        boolean result = filter.isApplicable(new UserDtoFilter("NN","947",0,2));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicable_CorrectParams() {
        boolean result = filter.isApplicable(new UserDtoFilter("NN","947",1,2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsers_Success() {
        User user = new User();
        user.setExperience(100);
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN","947",1,2);
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertTrue(result);
    }
    @Test
    public void testFilterUsers_Failure() {
        User user = new User();
        user.setExperience(2);
        UserDtoFilter userDtoFilter = new UserDtoFilter("NN","947",6,5);
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertFalse(result);
    }
}
