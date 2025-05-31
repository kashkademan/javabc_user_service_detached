package school.faang.user_service.exception.promotion;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class PromotionNotFoundException extends EntityNotFoundException {
    public PromotionNotFoundException(long promotionId) {
        super(String.format("Promotion with id %d not found", promotionId));
    }
    public PromotionNotFoundException(String promotionId) {
        super(String.format("Promotion with id %s not found", promotionId));
    }
}
