package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDtoFilter;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.subscription.PhonePatternFilter;

public class PhonePatternFilterTest {
    private final PhonePatternFilter filter = new PhonePatternFilter();

    @Test
    public void testIsApplicableIfPhoneNull(){
        boolean result = filter.isApplicable(new UserDtoFilter());
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicableIfPhoneBlank(){
        boolean result = filter.isApplicable(new UserDtoFilter("nn"," ",1,2));
        Assertions.assertFalse(result);
    }

    @Test
    public void testIsApplicableIfPhoneOk(){
        boolean result = filter.isApplicable(new UserDtoFilter("nn","111",1,2));
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsersSuccess(){
        UserDtoFilter userDtoFilter = new UserDtoFilter("nn","947",1,2);
        User user = new User();
        user.setPhone("947");
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertTrue(result);
    }

    @Test
    public void testFilterUsersFailure(){
        UserDtoFilter userDtoFilter = new UserDtoFilter("nn","948",1,2);
        User user = new User();
        user.setPhone("947");
        boolean result = filter.filterUsers(user,userDtoFilter);
        Assertions.assertFalse(result);
    }
}
