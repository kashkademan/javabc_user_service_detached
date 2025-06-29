package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.promotion.PromotionTariffNotFoundException;
import school.faang.user_service.repository.promotion.PromotionTariffRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionTariffService {
    private final PromotionTariffRepository promotionTariffRepository;

    @Transactional(readOnly = true)
    public PromotionTariff getPromotionTariffById(long tariffId) {
        return promotionTariffRepository.findById(tariffId)
                .orElseThrow(() -> {
                    log.error("Promotion tariff with id {} not found", tariffId);
                    return new PromotionTariffNotFoundException(tariffId);
                });
    }

    @Transactional(readOnly = true)
    public List<PromotionTariff> getAllActivePromotionTariff() {
        return promotionTariffRepository.findAllByDeletedFalse();
    }
}
