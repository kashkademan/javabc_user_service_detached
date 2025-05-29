package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;
import static school.faang.user_service.entity.promotion.PromotionType.EVENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final EventService eventService;
    private final PromotionTariffService promotionTariffService;
    private final PromotionRedisService promotionRedisService;

    @Transactional
    public Promotion createPromotion(final Long eventId, final Long tariffId) {

        Event event = eventService.getEvent(eventId);
        PromotionTariff tariff = promotionTariffService.getPromotionTariffById(tariffId);

        Promotion promotion = new Promotion();
        promotion.setType(EVENT);
        promotion.setEvent(event);
        promotion.setTariff(tariff);
        promotion.setCountView(tariff.getCountView());
        promotion.setEndDate(LocalDateTime.now().plusDays(tariff.getDurationDays()));
        promotion.setStatus(ACTIVE);


        Promotion savePromotion = promotionRepository.save(promotion);
        log.info("Promotion with id {} has been created", savePromotion.getId());


        // TODO: нужно подумать какой тип данныхвозвращать на клиент
        promotionRedisService.saveEventPromotion(promotion, event, tariff);


        return savePromotion;
    }
}
