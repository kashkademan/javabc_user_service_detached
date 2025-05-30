package school.faang.user_service.exception.promotion;

import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class PromotionNotFoundException extends EntityNotFoundException {
    public PromotionNotFoundException(UUID promotionId) {
        super(String.format("Promotion with id %s not found", promotionId.toString()));
    }
}
