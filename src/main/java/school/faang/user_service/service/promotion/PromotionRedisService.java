package school.faang.user_service.service.promotion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.RedisLockPromotionProperties;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionRedisMapper promotionRedisMapper;
    private final Executor executor;
    private final RedisLockPromotionProperties props;

    public PromotionRedisService(PromotionRedisRepository promotionRedisRepository,
                                 PromotionRedisMapper promotionRedisMapper,
                                 @Qualifier("decrementCountViewExecutorExecutor") Executor executor,
                                 RedisLockPromotionProperties props) {
        this.promotionRedisRepository = promotionRedisRepository;
        this.promotionRedisMapper = promotionRedisMapper;
        this.executor = executor;
        this.props = props;
    }

    public void savePromotion(Promotion promotion) {
        PromotionRedisModel promotionRedisModel = promotionRedisMapper.toEventPromotionRedis(promotion);
        log.debug("Mapping Promotion entity to PromotionRedisModel. Entity content: {}. RedisModel content: {}.",
                promotion, promotionRedisModel);

        String promotionKey = RedisKeyUtil.getKeyById(promotion.getId(), RedisHashType.PROMOTION);
        promotionRedisModel.setKey(promotionKey);

        PromotionRedisModel savedPromotion = promotionRedisRepository.save(promotionRedisModel);
        log.info("Promotion {} has been saved in redis", savedPromotion);
    }

    public List<PromotionRedisModel> getAllPromotions() {
        Iterable<PromotionRedisModel> iterable = promotionRedisRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .toList();
    }

    public void deletePromotionByKey(String promotionKey) {
        promotionRedisRepository.deleteById(promotionKey);
        log.info("Promotion with key {} has been deleted", promotionKey);
    }

    @Async("decrementCountViewExecutorExecutor")
    public void decrementCountViewByEventIds(List<Long> eventIds) {
        eventIds.forEach(eventId -> executor.execute(() -> {
            promotionRedisRepository.findByEventId(eventId)
                    .ifPresent(promotion -> decrementCountView(promotion.getKey()));
        }));
    }

    private void decrementCountView(String promotionKey) {
        promotionRedisRepository.decrementCountView(promotionKey);
    }
}
