package school.faang.user_service.validation.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.exception.promotion.ActivePromotionAlreadyExistsException;
import school.faang.user_service.repository.promotion.PromotionRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionValidatorTest {
    @InjectMocks
    private PromotionValidator promotionValidator;
    @Mock
    private PromotionRepository promotionRepository;

    @Test
    void testCheckActivePromotionForUser_noActivePromotion() {
        long userId = 1L;

        when(promotionRepository.existsByUserIdAndStatus(userId, PromotionStatus.ACTIVE)).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.checkActivePromotionForUser(userId));
    }

    @Test
    void testCheckActivePromotionForUser_activePromotionExists() {
        long userId = 1L;

        when(promotionRepository.existsByUserIdAndStatus(userId, PromotionStatus.ACTIVE)).thenReturn(true);

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionValidator.checkActivePromotionForUser(userId)
        );
    }

    @Test
    void testCheckActivePromotionForEvent_noActivePromotion() {
        long eventId = 10L;
        when(promotionRepository.existsByEventIdAndStatus(eventId, PromotionStatus.ACTIVE)).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.checkActivePromotionForEvent(eventId));
    }

    @Test
    void testCheckActivePromotionForEvent_activePromotionExists() {
        long eventId = 10L;

        when(promotionRepository.existsByEventIdAndStatus(eventId, PromotionStatus.ACTIVE)).thenReturn(true);

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionValidator.checkActivePromotionForEvent(eventId)
        );
    }
}
