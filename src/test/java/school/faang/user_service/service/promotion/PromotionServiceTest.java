package school.faang.user_service.service.promotion;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.exception.promotion.ActivePromotionAlreadyExistsException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.promotion.PromotionValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {
    @InjectMocks
    private PromotionService promotionService;
    @Mock
    private PromotionRepository promotionRepository;
    @Mock
    private EventService eventService;
    @Mock
    private PromotionTariffService promotionTariffService;
    @Mock
    private PromotionRedisService promotionRedisService;
    @Mock
    private PromotionValidator promotionValidator;
    private final Long eventId = 1L;
    private final Long tariffId = 2L;
    private final Long promotionId = 3L;
    private Event event;
    private PromotionTariff tariff;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(eventId);

        tariff = new PromotionTariff();
        tariff.setId(tariffId);
        tariff.setCountView(100);
        tariff.setDurationDays(5);

        promotion = new Promotion();
        promotion.setId(promotionId);
        promotion.setEvent(event);
        promotion.setTariff(tariff);
        promotion.setStatus(PromotionStatus.ACTIVE);
    }

    @Test
    void testGetPromotionById_promotionExists() {
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        Promotion result = promotionService.getPromotionById(promotionId);

        assertEquals(promotion, result);
        verify(promotionRepository, times(1)).findById(promotionId);
    }

    @Test
    void testGetPromotionById_promotionNotFound() {
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        assertThrows(PromotionNotFoundException.class,
                () -> promotionService.getPromotionById(promotionId));
    }

    @Test
    void tesCreatePromotion_createAndSavePromotion() {
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(promotionTariffService.getPromotionTariffById(tariffId)).thenReturn(tariff);
        when(promotionRepository.save(any(Promotion.class))).thenAnswer(invocation -> {
            Promotion saved = invocation.getArgument(0);
            saved.setId(promotionId);
            return saved;
        });

        Promotion result = promotionService.createPromotion(eventId, tariffId);

        assertNotNull(result);
        assertEquals(event, result.getEvent());
        assertEquals(tariff, result.getTariff());
        assertEquals(PromotionStatus.ACTIVE, result.getStatus());
        verify(promotionValidator).checkActivePromotionForEvent(eventId, tariffId);
//        verify(paymentService).sendPayment(tariff);
        verify(promotionRepository).save(any(Promotion.class));
//        verify(promotionRedisService).savePromotion(any(Promotion.class), eq(event));
    }

    @Test
    void testCreatePromotion_validationFails() {
        doThrow(new ActivePromotionAlreadyExistsException(eventId, PromotionType.EVENT))
                .when(promotionValidator).checkActivePromotionForEvent(eventId, tariffId);

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionService.createPromotion(eventId, tariffId)
        );
        verify(promotionValidator).checkActivePromotionForEvent(eventId, tariffId);
        verifyNoInteractions(eventService,
                promotionTariffService,
//                paymentService,
                promotionRepository, promotionRedisService);
    }

    @Test
    void testCreatePromotion_shouldThrow_whenPaymentFails() {
        when(eventService.getEventById(eventId)).thenReturn(event);
        when(promotionTariffService.getPromotionTariffById(tariffId)).thenReturn(tariff);

//        doThrow(FeignException.class)
//                .when(paymentService).sendPayment(tariff);

        assertThrows(
                FeignException.class,
                () -> promotionService.createPromotion(eventId, tariffId)
        );

        verify(promotionValidator).checkActivePromotionForEvent(eventId, tariffId);
        verify(eventService).getEventById(eventId);
        verify(promotionTariffService).getPromotionTariffById(tariffId);
        verifyNoMoreInteractions(promotionRepository, promotionRedisService);
    }

    @Test
    void testFinishedPromotionByView_shouldSetStatusAndSave() {
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        promotionService.finishPromotionByView(promotionId);

        assertEquals(PromotionStatus.FINISHED_VIEW, promotion.getStatus());
        verify(promotionRepository).save(promotion);
    }

    @Test
    void testFinishedPromotionByTime_shouldSetStatusAndSave() {
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(promotion));

        promotionService.finishPromotionByTime(promotionId);

        assertEquals(PromotionStatus.FINISHED_TIME, promotion.getStatus());
        verify(promotionRepository).save(promotion);
    }
}
