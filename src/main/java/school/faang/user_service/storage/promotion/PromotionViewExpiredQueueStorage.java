package school.faang.user_service.storage.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class PromotionViewExpiredQueueStorage {
    private final Queue<String> deletedPromotions = new ConcurrentLinkedQueue<>();

    public void addDeletedPromotion(String promotionId) {
        log.debug("Promotion with id {} added to expired-by-views promotions queue", promotionId);
        deletedPromotions.add(promotionId);
    }

    public String pollDeletedPromotion() {
        String promotionId = deletedPromotions.poll();
        log.debug("Promotion with id {} polled from expired-by-views promotions queue", promotionId);
        return promotionId;
    }

    public boolean hasDeletedPromotions() {
        return !deletedPromotions.isEmpty();
    }
}
