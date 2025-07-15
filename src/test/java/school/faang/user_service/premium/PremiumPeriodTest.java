package school.faang.user_service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.premium.PremiumPeriodEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тесты для перечисления PremiumPeriodEnum")
class PremiumPeriodTest {

    @Test
    @DisplayName("Метод getPremiumPeriod возвращает корректный enum для валидных дней")
    void getPremiumPeriod_shouldReturnCorrectEnum() {
        assertEquals(PremiumPeriodEnum.ONE_MONTH, PremiumPeriodEnum.getPremiumPeriod(30));
        assertEquals(PremiumPeriodEnum.THREE_MONTHS, PremiumPeriodEnum.getPremiumPeriod(90));
        assertEquals(PremiumPeriodEnum.ONE_YEAR, PremiumPeriodEnum.getPremiumPeriod(365));
    }

    @Test
    @DisplayName("Метод getPremiumPeriod выбрасывает исключение для неверных дней")
    void getPremiumPeriod_shouldThrow_whenInvalidDays() {
        assertThrows(IllegalArgumentException.class,
                () -> PremiumPeriodEnum.getPremiumPeriod(15));
    }
}