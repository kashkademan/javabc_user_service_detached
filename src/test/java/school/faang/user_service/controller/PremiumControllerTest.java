package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {
    private static final Long USER_ID = 1L;
    private static final Integer PREMIUM_DAYS = 30;
    private static final Integer INVALID_DAYS = 25;

    @Mock
    private PremiumService service;

    @InjectMocks
    private PremiumController controller;

    @Test
    public void testBuyPremium_whenValidParams_thenReturnSuccess() {
        controller.buyPremium(USER_ID, PREMIUM_DAYS);

        verify(service, times(1)).buyPremium(eq(USER_ID), eq(PremiumPeriod.fromDays(PREMIUM_DAYS)));
    }

    @Test
    public void testBuyPremium_whenInvalidDays_thenThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.buyPremium(USER_ID, INVALID_DAYS));
        verify(service, never()).buyPremium(eq(USER_ID), any());
    }
}