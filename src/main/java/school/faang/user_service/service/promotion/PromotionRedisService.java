package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.model.redis.event.EventRedisModel;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static school.faang.user_service.model.redis.RedisHashType.EVENT_PROMOTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final EventRedisRepository eventRedisRepository;
    private final PromotionRedisRepository promotionRedisRepository;
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    private final PromotionMapper promotionMapper;
    private final EventMapper eventMapper;

    // TODO: транзакция внутри транзакции
    @Transactional
    public void saveEventPromotion(Promotion promotion, Event event) {
        PromotionRedisModel promotionRedisModel = promotionMapper.toEventPromotionRedis(promotion);
        EventRedisModel eventRedisModel = eventMapper.toEventRedis(event);

        String eventId = EVENT_PROMOTION.getHashName() + ": " + eventRedisModel.getId();
        eventRedisModel.setId(eventId);
        eventRedisModel.setPromotionId(promotionRedisModel.getId());
        long ttlSecond = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        eventRedisModel.setTtl(ttlSecond);

        EventRedisModel saveEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event promotion {} has been saved in redis", saveEvent);
    }

    public List<Event> getPromotedEvents(EventFilter filter) {

        List<EventRedisModel> eventRedisModelList = getFilteredEventRedisModels(filter);

        eventRedisModelList.forEach(eventModel -> decrementCountView(eventModel.getId(), eventModel.getPromotionId()));
        
        return eventRedisModelList.stream()
                .map(eventMapper::toEventEntity)
                .toList();
    }

    private List<EventRedisModel> getFilteredEventRedisModels(EventFilter filter) {
        List<EventRedisModel> redisModels = StreamSupport
                .stream(eventRedisRepository.findAll().spliterator(), false)
                .toList();

        return redisModels.stream()
                .filter(eventRedis -> filter.getTitle() == null ||
                        eventRedis.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(eventRedis -> filter.getEventType() == null ||
                        Objects.equals(eventRedis.getType(), filter.getEventType()))
                .filter(eventRedis -> filter.getEventStatus() == null ||
                        Objects.equals(eventRedis.getStatus(), filter.getEventStatus()))
                .filter(eventRedis -> filter.getStartFrom() == null ||
                        !eventRedis.getStartDate().isBefore(filter.getStartFrom()))
                .filter(eventRedis -> filter.getStartTo() == null ||
                        !eventRedis.getStartDate().isAfter(filter.getStartTo()))
                // TODO: можно ли в redis not null constraint
                .sorted(Comparator.comparingInt(EventRedisModel::getCoefficientPriority))
                .toList();
    }

    // TODO: продумать обработку исключений
    private void decrementCountView(String eventId, String promotionId) {
        try {
            PromotionRedisModel promotion = promotionRedisRepository.findById(promotionId)
                    .orElseThrow(() -> {
                        log.error("Promotion with id {} not found", promotionId);
                        return new PromotionNotFoundException(promotionId);
                    });

            Integer decrementCountView = promotion.getCountView() - 1;

            if (Objects.equals(decrementCountView, 0)) {
                promotionViewExpiredQueueStorage.addDeletedPromotion(promotion.getId());
                eventRedisRepository.deleteById(eventId);
                promotionRedisRepository.deleteById(promotionId);
            } else {
                promotion.setCountView(decrementCountView);
                promotionRedisRepository.save(promotion);
            }
        } catch (PromotionNotFoundException ex) {
            eventRedisRepository.deleteById(eventId);
            log.info("Event with id {} has been deleted from redis", eventId);
            promotionViewExpiredQueueStorage.addDeletedPromotion(promotionId);
        }
    }
}
