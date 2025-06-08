package school.faang.user_service.exception.promotion;

import java.util.NoSuchElementException;
import java.util.UUID;

public class PromotionInCashNotFoundException extends NoSuchElementException {

    public PromotionInCashNotFoundException(UUID promotionKey) {
        super(String.format("Promotion with id %s not found", promotionKey));
    }
}
