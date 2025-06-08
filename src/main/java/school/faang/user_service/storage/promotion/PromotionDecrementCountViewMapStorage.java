package school.faang.user_service.storage.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
// TODO: логи
public class PromotionDecrementCountViewMapStorage {
    private final Map<Long, List<Long>> decrementCountPromotions = new ConcurrentHashMap<>();

    public void putDecrementCountPromotions(Long num, List<Long> promotionIds) {
//        log.debug("Promotion with id {} added to expired-by-time promotions queue", promotionId);
        decrementCountPromotions.put(num, promotionIds);
    }

    public List<Long> removeDecrementCountPromotions(Long num) {
        List<Long> promotionIds = decrementCountPromotions.remove(num);
//        log.debug("Promotion with id {} polled from expired-by-time promotions queue", promotionId);
        return promotionIds;
    }

//    public boolean hasDeletedPromotions() {
//        return !deletedPromotions.isEmpty();
//    }
}
