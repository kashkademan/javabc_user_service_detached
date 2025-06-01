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
class PromotionValidatorTest {
    @InjectMocks
    private PromotionValidator promotionValidator;
    @Mock
    private PromotionRepository promotionRepository;

    @Test
    void testCheckActivePromotionForUser_noActivePromotion() {
        long userId = 1L;
        long tariffId = 2L;

        when(promotionRepository.existsByUserIdAndStatus(userId, PromotionStatus.ACTIVE)).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.checkActivePromotionForUser(userId, tariffId));
    }

    @Test
    void checkActivePromotionForUser_activePromotionExists() {
        long userId = 1L;
        long tariffId = 2L;

        when(promotionRepository.existsByUserIdAndStatus(userId, PromotionStatus.ACTIVE)).thenReturn(true);

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionValidator.checkActivePromotionForUser(userId, tariffId)
        );
    }

    @Test
    void testCheckActivePromotionForEvent_noActivePromotion() {
        long eventId = 10L;
        long tariffId = 20L;

        when(promotionRepository.existsByEventIdAndStatus(eventId, PromotionStatus.ACTIVE)).thenReturn(false);

        assertDoesNotThrow(() -> promotionValidator.checkActivePromotionForEvent(eventId, tariffId));
    }

    @Test
    void testCheckActivePromotionForEvent_activePromotionExists() {
        long eventId = 10L;
        long tariffId = 20L;

        when(promotionRepository.existsByEventIdAndStatus(eventId, PromotionStatus.ACTIVE)).thenReturn(true);

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionValidator.checkActivePromotionForEvent(eventId, tariffId)
        );
    }
}
