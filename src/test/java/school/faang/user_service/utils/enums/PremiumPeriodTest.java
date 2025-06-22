package school.faang.user_service.utils.enums;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.common.PreConditionFailedException;
import school.faang.user_service.util.enums.PremiumPeriod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PremiumPeriodTest {
    @Test
    void fromDays_shouldReturnMonth_when30Days() {
        PremiumPeriod period = PremiumPeriod.fromDays(30);

        assertEquals(PremiumPeriod.MONTH, period);
    }

    @Test
    void fromDays_shouldReturnThreeMonths_when90Days() {
        PremiumPeriod period = PremiumPeriod.fromDays(90);

        assertEquals(PremiumPeriod.THREE_MONTHS, period);
    }

    @Test
    void fromDays_shouldReturnYear_when365Days() {
        PremiumPeriod period = PremiumPeriod.fromDays(365);

        assertEquals(PremiumPeriod.YEAR, period);
    }

    @Test
    void fromDays_shouldThrowException_whenInvalidDaysGiven() {
        int invalidDays = 45;

        assertThrows(PreConditionFailedException.class,() -> PremiumPeriod.fromDays(invalidDays));
    }
}
