package school.faang.user_service.service.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.user.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final UserService userService;
    private final EventService eventService;

    public Promotion createPromotion(final Promotion promotion, Long eventId) {

        // TODO: подумать, возможно стоит разделить на две ручки
        if (eventId != null) {
            Event event = eventService.getEvent(eventId);
            promotion.setEvent(event);
        } else {
            User currentUser = userService.getCurrentUser();
            promotion.setUser(currentUser);
        }

        Promotion savePromotion = promotionRepository.save(promotion);
        log.info("Promotion with id {} has been created", savePromotion.getId());

        return savePromotion;
    }
}
