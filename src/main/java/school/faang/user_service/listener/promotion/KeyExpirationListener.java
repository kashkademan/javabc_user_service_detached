package school.faang.user_service.listener.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

@Component
@Slf4j
public class KeyExpirationListener extends KeyExpirationEventMessageListener {
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    public KeyExpirationListener(RedisMessageListenerContainer listenerContainer,
                                 PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage) {
        super(listenerContainer);
        this.promotionViewExpiredQueueStorage = promotionViewExpiredQueueStorage;
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (key.startsWith(RedisHashType.PROMOTION.getHashName())){
            promotionViewExpiredQueueStorage.addDeletedPromotion(key);
        }
    }
}
