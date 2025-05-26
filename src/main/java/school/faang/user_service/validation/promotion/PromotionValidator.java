package school.faang.user_service.validation.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.repository.promotion.PromotionRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionValidator {
    private final PromotionRepository promotionRepository;


    public void checkActivePromotion(final Promotion promotion) {
        // TODO: проверка что сейчас нет активного промоушена у юзера или события
        if (promotion.getEvent() != null) {
            checkActivePromotionForEvent(promotion);
        } else if (promotion.getUser() != null) {
            checkActivePromotionForUser(promotion);
        }
    }

    private void checkActivePromotionForUser(Promotion promotion) {
        boolean isActivePromotionForUser =
                promotionRepository.existsActivePromotionByEvent(promotion.getEvent().getId());
        if (isActivePromotionForUser) {
            // TODO: поменять исключение
            throw new RuntimeException();
        }
    }

    private void checkActivePromotionForEvent(Promotion promotion) {
        boolean isActivePromotionForEvent =
                promotionRepository.existsActivePromotionByUser(promotion.getUser().getId());
        if (isActivePromotionForEvent) {
            throw new RuntimeException();
        }
    }
}
