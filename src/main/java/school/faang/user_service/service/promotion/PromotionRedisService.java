package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.model.redis.promotion.EventRedisModel;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final EventRedisRepository eventRedisRepository;
    private final PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    private final PromotionMapper promotionMapper;
    private final EventMapper eventMapper;

    // TODO: транзакция внутри транзакции
    @Transactional
    public void saveEventPromotion(Promotion promotion, Event event) {
        EventRedisModel eventRedisModel = eventMapper.toEventRedis(event);
        PromotionRedisModel promotionRedisModel = promotionMapper.toEventPromotionRedis(promotion);

        EventRedisModel saveEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been saved in redis", saveEvent);
        // TODO: сохранять ещё и promotionId
        // TODO: условие на TTL
        long seconds = Duration.between(LocalDateTime.now(), promotion.getEndDate()).getSeconds();
        promotionRedisModel.setTtl(Math.max(seconds, 0)); // защита от отрицательных значений
        PromotionRedisModel savePromotion = promotionRedisRepository.save(promotionRedisModel);
        log.info("Promotion {} has been saved in redis", savePromotion);
    }

    // TODO: коэфициенты по тарифу
    // TODO: отнимать счётчик
    public List<Event> getPromotedEvents(EventFilter filter) {

        List<EventRedisModel> eventRedisModelList = getFilteredEventRedisModels(filter);

        List<String> promotionIds = eventRedisModelList.stream()
                .map(EventRedisModel::getPromotionId)
                .toList();

        promotionIds.forEach(this::decrementCountView);
        
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
                .sorted(Comparator.comparingInt(EventRedisModel::getCoefficientPriority).reversed())
                .toList();
    }

    private void decrementCountView(String promotionId) {
        PromotionRedisModel promotionRedisModel =
                promotionRedisRepository.findById(promotionId).orElseThrow(RuntimeException::new);

        Integer decrementCountView = promotionRedisModel.getCountView() - 1;

        if (Objects.equals(decrementCountView, 0)) {
            promotionViewExpiredQueueStorage.addDeletedPromotion(promotionRedisModel.getId());
            promotionRedisRepository.deleteById(promotionId);
        } else {
            promotionRedisModel.setCountView(decrementCountView);
            promotionRedisRepository.save(promotionRedisModel);
        }
    }
}
