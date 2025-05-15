package school.faang.user_service.validator.subscription;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSubscriptionValidation {

    @Test
    public void testValidateFollowActionSameId() {
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class,
                () -> SubscriptionValidation.validateFollowAction(followerId, followeeId));
    }

    @Test
    public void testValidateUnfollowActionSameId() {
        long followerId = 1L;
        long followeeId = 1L;

        assertThrows(DataValidationException.class,
                () -> SubscriptionValidation.validateUnfollowAction(followerId, followeeId));
    }

    @Test
    public void testValidateSubscribeActionSubExists() {
        boolean existSub = true;

        assertThrows(DataValidationException.class, () -> SubscriptionValidation.validateSubscribeAction(existSub));
    }

    @Test
    public void testValidateUnsubscribeActionSubExists() {
        boolean existSub = false;

        assertThrows(DataValidationException.class, () -> SubscriptionValidation.validateUnsubscribeAction(existSub));
    }

}
