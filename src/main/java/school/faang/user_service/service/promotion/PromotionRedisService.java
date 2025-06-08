package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.promotion.PromotionInCashNotFoundException;
import school.faang.user_service.mapper.promotion.PromotionRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.event.EventRedisModel;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    private final PromotionRedisMapper promotionRedisMapper;

    public void savePromotion(Promotion promotion) {
        PromotionRedisModel promotionRedisModel = promotionRedisMapper.toEventPromotionRedis(promotion);
        // TODO: перенести в фасад
        log.info("Mapping");

        UUID promotionKey = RedisKeyUtil.getKeyById(promotion.getId(), RedisHashType.PROMOTION);
        promotionRedisModel.setKey(promotionKey);

        PromotionRedisModel savedPromotion = promotionRedisRepository.save(promotionRedisModel);
        log.info("Promotion {} has been saved in redis", savedPromotion);
    }

    // TODO: удалить

//    public List<Event> getPromotedEvents(EventFilter filter) {
//
//        List<EventRedisModel> eventRedisModelList = getFilteredEventRedisModels(filter);
//
//        eventRedisModelList.forEach(eventModel -> decrementCountView(eventModel.getId(), eventModel.getPromotionId()));
//
//        return eventRedisModelList.stream()
//                .map(eventRedisMapper::toEventEntity)
//                .toList();
//    }

//    private List<EventRedisModel> getFilteredEventRedisModels(EventFilter filter) {
//        List<EventRedisModel> redisModels = StreamSupport
//                .stream(eventRedisRepository.findAll().spliterator(), false)
//                .toList();
//
//        return redisModels.stream()
//                .filter(eventRedis -> filter.getTitle() == null ||
//                        eventRedis.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
//                .filter(eventRedis -> filter.getEventType() == null ||
//                        Objects.equals(eventRedis.getType(), filter.getEventType()))
//                .filter(eventRedis -> filter.getEventStatus() == null ||
//                        Objects.equals(eventRedis.getStatus(), filter.getEventStatus()))
//                .filter(eventRedis -> filter.getStartFrom() == null ||
//                        !eventRedis.getStartDate().isBefore(filter.getStartFrom()))
//                .filter(eventRedis -> filter.getStartTo() == null ||
//                        !eventRedis.getStartDate().isAfter(filter.getStartTo()))
//                .sorted(Comparator.comparingInt(EventRedisModel::getCoefficientPriority))
//                .toList();
//    }

    // TODO: в цикле
    @Async()
    public void decrementCountViewByEventIds(List<Long> eventIds) {
        eventIds.forEach(eventId -> {
            Optional<PromotionRedisModel> optionalPromotion = promotionRedisRepository.findByEventId(eventId);

            if (optionalPromotion.isEmpty()) {
                return;
            }
            decrementCountView(optionalPromotion.get());
        });
// TODO: удалить исключение
    }

    public void decrementCountView(PromotionRedisModel promotion) {
        int newCount = promotion.getCountView() - 1;

        promotion.setCountView(newCount);
        promotionRedisRepository.save(promotion);
    }
}
