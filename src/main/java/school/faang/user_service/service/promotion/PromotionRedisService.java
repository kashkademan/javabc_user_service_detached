package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.mapper.promotion.PromotionTariffMapper;
import school.faang.user_service.model.redis.promotion.EventPromotionRedis;
import school.faang.user_service.model.redis.promotion.EventRedis;
import school.faang.user_service.model.redis.promotion.PromotionTariffRedis;
import school.faang.user_service.repository.promotion.EventPromotionRedisRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionRedisService {
    private final EventPromotionRedisRepository eventPromotionRedisRepository;
    private final PromotionMapper promotionMapper;
    private final PromotionTariffMapper promotionTariffMapper;
    private final EventMapper eventMapper;

    public EventPromotionRedis saveEventPromotion(Promotion promotion, Event event, PromotionTariff tariff) {
        PromotionTariffRedis promotionTariffRedis = promotionTariffMapper.toPromotionTariffRedis(tariff);
        EventRedis eventRedis = eventMapper.toEventRedis(event);
        EventPromotionRedis eventPromotionRedis = promotionMapper.toEventPromotionRedis(promotion);
        eventPromotionRedis.setTariff(promotionTariffRedis);
        eventPromotionRedis.setEvent(eventRedis);

        EventPromotionRedis saveEventPromotion = eventPromotionRedisRepository.save(eventPromotionRedis);
        log.info("Promotion {} has been saved in redis", saveEventPromotion);
        return saveEventPromotion;
    }
}
