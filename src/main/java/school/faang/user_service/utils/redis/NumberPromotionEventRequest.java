package school.faang.user_service.utils.redis;

import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicLong;

@UtilityClass
public class NumberPromotionEventRequest {
    private static final AtomicLong NUMBER_REQUEST = new AtomicLong(0);
    public long getNextNumber() {
       return NUMBER_REQUEST.incrementAndGet();
    }
}
