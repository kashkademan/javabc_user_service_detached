package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionRedisMapper promotionRedisMapper;

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

    @Async("decrementCountViewExecutorExecutor")
    public void decrementCountViewByEventIds(List<Long> eventIds) {
        eventIds.forEach(eventId -> {
            Optional<PromotionRedisModel> optionalPromotion = promotionRedisRepository.findByEventId(eventId);

            if (optionalPromotion.isEmpty()) {
                return;
            }
            decrementCountView(optionalPromotion.get());
        });
    }

    public void decrementCountView(PromotionRedisModel promotion) {
        int newCount = promotion.getCountView() - 1;
        promotion.setCountView(newCount);
        promotionRedisRepository.save(promotion);
        log.debug("Promotion with key {} has been updated count view, {}", promotion.getKey(), newCount);
    }

    public void deletePromotionByKey(String promotionKey) {
        promotionRedisRepository.deleteById(promotionKey);
        log.info("Promotion with key {} has been deleted", promotionKey);
    }
}
