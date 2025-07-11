package school.faang.user_service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.premium.PremiumPeriod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тесты для перечисления PremiumPeriod")
class PremiumPeriodTest {

    @Test
    @DisplayName("Метод getPremiumPeriod возвращает корректный enum для валидных дней")
    void getPremiumPeriod_shouldReturnCorrectEnum() {
        assertEquals(PremiumPeriod.ONE_MONTH, PremiumPeriod.getPremiumPeriod(30));
        assertEquals(PremiumPeriod.THREE_MONTHS, PremiumPeriod.getPremiumPeriod(90));
        assertEquals(PremiumPeriod.ONE_YEAR, PremiumPeriod.getPremiumPeriod(365));
    }

    @Test
    @DisplayName("Метод getPremiumPeriod выбрасывает исключение для неверных дней")
    void getPremiumPeriod_shouldThrow_whenInvalidDays() {
        assertThrows(IllegalArgumentException.class,
                () -> PremiumPeriod.getPremiumPeriod(15));
    }
}