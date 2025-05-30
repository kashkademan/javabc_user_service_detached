package school.faang.user_service.listener.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

import java.util.UUID;

import static school.faang.user_service.model.redis.promotion.RedisHashType.EVENT_PROMOTION;
import static school.faang.user_service.model.redis.promotion.RedisHashType.USER_PROMOTION;

@Component
@Slf4j
public class KeyExpirationListener extends KeyExpirationEventMessageListener {
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    public KeyExpirationListener(RedisMessageListenerContainer listenerContainer,
                                 PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage) {
        super(listenerContainer);
        this.promotionViewExpiredQueueStorage = promotionViewExpiredQueueStorage;
    }

    // TODO: нужно как-то получить promotion id
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (key.startsWith(EVENT_PROMOTION.getHashName()) || key.startsWith(USER_PROMOTION.getHashName())) {
            promotionViewExpiredQueueStorage.addDeletedPromotion(UUID.fromString(key));
        }
    }
}
