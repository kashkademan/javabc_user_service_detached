package school.faang.user_service.exception.promotion;

import jakarta.persistence.EntityNotFoundException;

public class PromotionTariffNotFoundException extends EntityNotFoundException {
    public PromotionTariffNotFoundException(long tariffId) {
        super(String.format("Promotion tariff with id %d not found", tariffId));
    }
}
