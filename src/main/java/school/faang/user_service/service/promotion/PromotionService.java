package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;
import static school.faang.user_service.entity.promotion.PromotionStatus.FINISHED_TIME;
import static school.faang.user_service.entity.promotion.PromotionStatus.FINISHED_VIEW;
import static school.faang.user_service.entity.promotion.PromotionType.EVENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final EventService eventService;
    private final PromotionTariffService promotionTariffService;
    private final PromotionRedisService promotionRedisService;

    @Transactional(readOnly = true)
    public Promotion getPromotionById(long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> {
                    log.error("Promotion with id {} not found", promotionId);
                    return new PromotionNotFoundException(promotionId);
                });
    }

    // TODO: проверка, что при создании события существует и что на него нет активного события
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

        promotionRedisService.saveEventPromotion(promotion, event);

        return savePromotion;
    }

    @Transactional
    public void finishedPromotionByView(long promotionId) {
        Promotion promotion = getPromotionById(promotionId);

        promotion.setStatus(FINISHED_VIEW);
        promotionRepository.save(promotion);
        log.info("Promotion with id {} finished by view", promotionId);
    }

    @Transactional
    public void finishedPromotionByTime(long promotionId) {
        Promotion promotion = getPromotionById(promotionId);

        promotion.setStatus(FINISHED_TIME);
        promotionRepository.save(promotion);
        log.info("Promotion with id {} finished by time", promotionId);
    }
}
