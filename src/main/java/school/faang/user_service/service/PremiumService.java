package school.faang.user_service.service;

import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.enums.PremiumPeriod;

public interface PremiumService {
    PremiumDto buyPremium(long userId, PremiumPeriod period);

    void removePremium(int batchSize);
}