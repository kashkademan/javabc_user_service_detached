package school.faang.user_service.validation.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.promotion.ActivePromotionAlreadyExistsException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.promotion.PromotionTariffService;
import school.faang.user_service.service.user.UserService;

import static school.faang.user_service.entity.promotion.PromotionStatus.ACTIVE;
import static school.faang.user_service.entity.promotion.PromotionType.USER;
import static school.faang.user_service.entity.promotion.PromotionType.EVENT;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionValidator {
    private final PromotionRepository promotionRepository;

    public void checkActivePromotionForUser(long userId, long tariffId) {
        boolean isActivePromotionForUser = promotionRepository.existsByUserIdAndStatus(userId, ACTIVE);
        if (isActivePromotionForUser) {
            log.error("Active promotion already exists for user with id {}", userId);
            throw new ActivePromotionAlreadyExistsException(userId, USER);
        }
    }

    public void checkActivePromotionForEvent(long eventId, long tariffId) {
        boolean isActivePromotionForEvent = promotionRepository.existsByEventIdAndStatus(eventId, ACTIVE);
        if (isActivePromotionForEvent) {
            log.error("Active promotion already exists for event with id {}", eventId);
            throw new ActivePromotionAlreadyExistsException(eventId, EVENT);
        }
    }
}
