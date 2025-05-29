package school.faang.user_service.exception.promotion;

import jakarta.persistence.EntityNotFoundException;

public class PromotionNotFoundException extends EntityNotFoundException {
    public PromotionNotFoundException(long promotionId) {
        super(String.format("Promotion with id %d not found", promotionId));
    }
}
