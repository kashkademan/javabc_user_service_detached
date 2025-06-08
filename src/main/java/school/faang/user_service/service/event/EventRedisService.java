package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventRedisMapper;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.event.EventRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRedisService {
    private final EventRedisRepository eventRedisRepository;
    private final PromotionRedisRepository promotionRedisRepository;
    private final EventRedisMapper eventRedisMapper;

    public void saveEvent(Event event, long ttl) {
        EventRedisModel eventRedisModel = eventRedisMapper.toEventRedis(event);
        eventRedisModel.setTtl(ttl);

        EventRedisModel savedEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been saved in redis", savedEvent);
    }

    public Optional<Event> getEventById(long eventId) {
        UUID eventKey = RedisKeyUtil.getKeyById(eventId, RedisHashType.EVENT);

        return eventRedisRepository.findById(eventKey)
                .map(eventRedisMapper::toEventEntity)
                .or(Optional::empty);
    }



    // TODO: перенести в eventRedisService
    public void updatePromotedEvent(Event event) {
        EventRedisModel eventRedisModel = eventRedisMapper.toEventRedis(event);
        EventRedisModel savedEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been updated in redis", savedEvent);
    }
}
