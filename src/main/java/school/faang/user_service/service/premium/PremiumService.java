package school.faang.user_service.service.premium;

import school.faang.user_service.dto.entity.premium.PremiumPeriod;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PremiumService {
    PremiumDto buyPremium(long userid, long paymentNumber, PremiumPeriod premiumPeriod);

    CompletableFuture<Void> removeExpiredPremiumAccess(List<Premium> premiums);
}
