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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final PromotionRedisRepository promotionRedisRepository;
    private final EventRedisRepository eventRedisRepository;
    private final PromotionMapper promotionMapper;
    private final EventMapper eventMapper;
    private static final int NUM = 5;

    @Transactional
    public void saveEventPromotion(Promotion promotion, Event event) {
        EventRedisModel eventRedisModel = eventMapper.toEventRedis(event);
        PromotionRedisModel promotionRedisModel = promotionMapper.toEventPromotionRedis(promotion);

        EventRedisModel saveEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been saved in redis", saveEvent);
        PromotionRedisModel savePromotion = promotionRedisRepository.save(promotionRedisModel);
        log.info("Promotion {} has been saved in redis", savePromotion);
    }

    // TODO: коэфициенты по тарифу
    // TODO: отнимать счётчик
    public List<Event> getPromotedEvents(EventFilter filter) {
        eventRedisRepository.findAll();
        List<Event> events = new ArrayList<>();
//        List<Event> eventList = events.stream()
//                .filter(event -> !filteredEvents.contains(event))
//                .limit(NUM)
//                .toList();
        return events;
    }
}
