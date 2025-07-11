package school.faang.user_service.entity.premium;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * PremiumPeriod — перечисление, представляющее варианты премиум-подписок с их длительностью и стоимостью.
 * <p>
 * Каждое значение enum описывает конкретный период подписки:
 * <ul>
 *     <li>{@link #ONE_MONTH} — подписка на 30 дней с ценой 10 долларов;</li>
 *     <li>{@link #THREE_MONTHS} — подписка на 90 дней с ценой 25 долларов;</li>
 *     <li>{@link #ONE_YEAR} — подписка на 365 дней с ценой 80 долларов.</li>
 * </ul>
 * <p>
 * Метод {@link #getPremiumPeriod(int)} позволяет получить значение enum по количеству дней подписки.
 * Если передано значение, не соответствующее ни одному из периодов,
 * выбрасывается исключение {@link IllegalArgumentException}.
 * </p>
 * <p>
 * Используется для валидации и расчёта стоимости подписки при покупке премиума.
 * </p>
 *
 * @author agent
 * @since 10.07.2025
 */
@AllArgsConstructor
@Getter
public enum PremiumPeriod {
    ONE_MONTH(30, 10),
    THREE_MONTHS(90, 25),
    ONE_YEAR(365, 80);

    private final int days;
    private final int price;

    public static PremiumPeriod getPremiumPeriod(int days) {
        return Arrays.stream(values())
                .filter(period -> period.days == days)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid premium period: " + days));
    }
}