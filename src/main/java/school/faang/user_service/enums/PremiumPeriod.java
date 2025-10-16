package school.faang.user_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PremiumPeriod {
    MONTHLY(30, new BigDecimal("10.00")),
    QUARTERLY(90, new BigDecimal("25.00")),
    YEARLY(365, new BigDecimal("80.00"));

    private final int days;
    private final BigDecimal amount;

    public static PremiumPeriod fromDays(int days) {
        return Arrays.stream(values())
                .filter(s -> s.days == days)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported premium period days: " + days));
    }
}
