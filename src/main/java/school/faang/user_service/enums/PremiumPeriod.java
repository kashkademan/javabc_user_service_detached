package school.faang.user_service.enums;

import lombok.Getter;

@Getter
public enum PremiumPeriod {
    ONE_MONTH_PREMIUM(30, 10.0),
    THREE_MONTHS_PREMIUM(90, 25.0),
    ONE_YEAR_PREMIUM(365, 80.0);

    private final int days;
    private final double price;

    PremiumPeriod(int days, double price) {
        this.days = days;
        this.price = price;
    }

    public static PremiumPeriod fromDays(int days) {
        for (PremiumPeriod period : values()) {
            if (period.getDays() == days) {
                return period;
            }
        }
        throw new IllegalArgumentException("No PremiumPeriod found for %d days".formatted(days));
    }
}