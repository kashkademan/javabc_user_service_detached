package school.faang.user_service.util.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.exception.common.PreConditionFailedException;

import java.util.Arrays;

import static school.faang.user_service.util.LogsConstants.SUBSCRIPTION_NOT_FOUND;

@Getter
@Slf4j
public enum PremiumPeriod {
    MONTH(30, 10),
    THREE_MONTHS(90, 25),
    YEAR(365, 80);

    private final int daysAmount;
    private final double price;

    PremiumPeriod(int daysAmount, double price) {
        this.daysAmount = daysAmount;
        this.price = price;
    }

    public static PremiumPeriod fromDays(int daysAmount) {
        return Arrays.stream(PremiumPeriod.values())
                .filter(period -> period.getDaysAmount() == daysAmount)
                .findFirst()
                .orElseThrow(() -> {
                    log.error(String.format(SUBSCRIPTION_NOT_FOUND, daysAmount));
                    return new PreConditionFailedException(String.format(SUBSCRIPTION_NOT_FOUND, daysAmount));
                });
    }
}
