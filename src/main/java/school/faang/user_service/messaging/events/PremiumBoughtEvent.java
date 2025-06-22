package school.faang.user_service.messaging.events;

import school.faang.user_service.enums.Currency;
import school.faang.user_service.enums.PremiumPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PremiumBoughtEvent(Long userId, BigDecimal price, Currency currency, PremiumPeriod premiumPeriod, LocalDateTime startDate) {
}