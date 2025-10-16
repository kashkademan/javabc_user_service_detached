package school.faang.user_service.service.career;

import org.junit.Assert;
import org.junit.Test;

public class CareerServiceTest {
    private CareerService careerService;

    @Test
    public void testNullTitleIsInvalid() {
        Assert.assertThrows(
                IllegalArgumentException.class,
                () -> careerService.c
        );
    }
}