package school.faang.user_service.storage.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@Slf4j
public class PromotionViewExpiredQueueStorage {
    private final Queue<UUID> deletedPromotions = new ConcurrentLinkedQueue<>();

    public void addDeletedPromotion(UUID promotionId) {
        log.debug("Promotion with id {} added to expired-by-views promotions queue", promotionId);
        deletedPromotions.add(promotionId);
    }

    public UUID pollDeletedPromotion() {
        UUID promotionId = deletedPromotions.poll();
        log.debug("Promotion with id {} polled from expired-by-views promotions queue", promotionId);
        return promotionId;
    }

    public boolean hasDeletedPromotions() {
        return !deletedPromotions.isEmpty();
    }
}
