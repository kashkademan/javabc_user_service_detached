package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.repository.promotion.PromotionTariffRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionTariffService {
    private final PromotionTariffRepository promotionTariffRepository;

    @Transactional(readOnly = true)
    public List<PromotionTariff> getAllActivePromotionTariff() {
        return promotionTariffRepository.findAllByDeletedFalse();
    }
}
