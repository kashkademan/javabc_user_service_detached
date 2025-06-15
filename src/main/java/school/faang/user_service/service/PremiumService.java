package school.faang.user_service.service;

import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;

import java.util.concurrent.CompletableFuture;

public interface PremiumService {
    PremiumDto buyPremium(long userId, PremiumPeriod period);

    CompletableFuture<String> removePremium();
}
