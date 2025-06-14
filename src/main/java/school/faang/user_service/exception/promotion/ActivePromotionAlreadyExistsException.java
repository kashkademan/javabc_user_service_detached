package school.faang.user_service.exception.promotion;

import school.faang.user_service.entity.promotion.PromotionType;

public class ActivePromotionAlreadyExistsException extends RuntimeException {
    public ActivePromotionAlreadyExistsException(Long eventId, PromotionType type) {
        super(String.format("Active promotion already exists for %s with id %d", type.toString(), eventId));
    }
}
