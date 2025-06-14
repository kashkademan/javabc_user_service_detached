package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.PromotionType;
import school.faang.user_service.exception.event.EventNotFoundException;
import school.faang.user_service.exception.promotion.ActivePromotionAlreadyExistsException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.exception.promotion.PromotionTariffNotFoundException;
import school.faang.user_service.repository.promotion.PromotionRepository;
import school.faang.user_service.service.event.EventRedisService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.promotion.PromotionValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionServiceTest {
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
    @Mock
    private EventRedisService eventRedisService;
    @Captor
    private ArgumentCaptor<Promotion> promotionCaptor;
    private Event event;
    private PromotionTariff tariff;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1L);

        tariff = new PromotionTariff();
        tariff.setId(2L);
        tariff.setCountView(100);
        tariff.setDurationDays(5);

        promotion = new Promotion();
        promotion.setId(3L);
        promotion.setEvent(event);
        promotion.setTariff(tariff);
        promotion.setStatus(PromotionStatus.ACTIVE);
    }

    @Test
    void testGetPromotionById_promotionExists() {
        when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));

        Promotion result = promotionService.getPromotionById(promotion.getId());

        assertEquals(promotion, result);
        verify(promotionRepository, times(1)).findById(promotion.getId());
    }

    @Test
    void testGetPromotionById_promotionNotFound() {
        when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.empty());

        assertThrows(PromotionNotFoundException.class,
                () -> promotionService.getPromotionById(promotion.getId()));
    }

    @Test
    void tesCreatePromotion_createAndSavePromotion() {
        when(eventService.getEventById(event.getId())).thenReturn(event);
        when(promotionTariffService.getPromotionTariffById(tariff.getId())).thenReturn(tariff);
        when(promotionRepository.save(promotionCaptor.capture())).thenAnswer(invocation -> {
            Promotion saved = promotionCaptor.getValue();
            saved.setId(promotion.getId());
            return saved;
        });

        Promotion result = promotionService.createPromotion(event.getId(), tariff.getId());

        assertNotNull(result);
        assertEquals(result, promotionCaptor.getValue());
        assertEquals(event, result.getEvent());
        assertEquals(tariff, result.getTariff());
        assertEquals(PromotionStatus.ACTIVE, result.getStatus());
        verify(eventService).getEventById(event.getId());
        verify(promotionTariffService).getPromotionTariffById(tariff.getId());
        verify(promotionValidator).checkActivePromotionForEvent(event.getId());
        verify(promotionRepository).save(promotionCaptor.getValue());
        verify(promotionRedisService).savePromotion(promotionCaptor.getValue());
        verify(eventRedisService).saveEvent(eq(event), anyLong());
    }

    @Test
    void testCreatePromotion_validationFails() {
        doThrow(new ActivePromotionAlreadyExistsException(event.getId(), PromotionType.EVENT))
                .when(promotionValidator).checkActivePromotionForEvent(event.getId());

        assertThrows(
                ActivePromotionAlreadyExistsException.class,
                () -> promotionService.createPromotion(event.getId(), tariff.getId())
        );
        verify(promotionValidator).checkActivePromotionForEvent(event.getId());
        verifyNoInteractions(
                promotionRepository,
                promotionRedisService,
                eventRedisService
        );
    }

    @Test
    void testCreatePromotion_eventNotFound() {
        when(eventService.getEventById(event.getId())).thenThrow(EventNotFoundException.class);

        assertThrows(
                EventNotFoundException.class,
                () -> promotionService.createPromotion(event.getId(), tariff.getId())
        );
        verify(promotionValidator).checkActivePromotionForEvent(event.getId());
        verify(eventService).getEventById(event.getId());
        verifyNoInteractions(
                promotionRepository,
                promotionRedisService,
                eventRedisService
        );
    }

    @Test
    void testCreatePromotion_tariffNotFound() {
        when(promotionTariffService.getPromotionTariffById(tariff.getId()))
                .thenThrow(PromotionTariffNotFoundException.class);

        assertThrows(
                PromotionTariffNotFoundException.class,
                () -> promotionService.createPromotion(event.getId(), tariff.getId())
        );
        verify(promotionValidator).checkActivePromotionForEvent(event.getId());
        verify(promotionTariffService).getPromotionTariffById(tariff.getId());
        verifyNoInteractions(
                promotionRepository,
                promotionRedisService,
                eventRedisService
        );
    }

    @Test
    void testFinishedPromotionByView_setStatusFinishedViewAndSave() {
        when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));

        promotionService.finishPromotionByView(promotion.getId());

        assertEquals(PromotionStatus.FINISHED_VIEW, promotion.getStatus());
        verify(promotionRepository).save(promotion);
    }

    @Test
    void testFinishedPromotionByView_PromotionNotFound() {
        when(promotionRepository.findById(promotion.getId()))
                .thenThrow(PromotionNotFoundException.class);

        assertThrows(
                PromotionNotFoundException.class,
                () -> promotionService.finishPromotionByView(promotion.getId())
        );
        verify(promotionRepository, never()).save(promotion);
    }

    @Test
    void testFinishedPromotionByTime_setStatusFinishedTimeAndSave() {
        when(promotionRepository.findById(promotion.getId())).thenReturn(Optional.of(promotion));

        promotionService.finishPromotionByTime(promotion.getId());

        assertEquals(PromotionStatus.FINISHED_TIME, promotion.getStatus());
        verify(promotionRepository).save(promotion);
    }

    @Test
    void testFinishedPromotionByTime_PromotionNotFound() {
        when(promotionRepository.findById(promotion.getId()))
                .thenThrow(PromotionNotFoundException.class);

        assertThrows(
                PromotionNotFoundException.class,
                () -> promotionService.finishPromotionByTime(promotion.getId())
        );
        verify(promotionRepository, never()).save(promotion);
    }

    @Test
    void testGetAllActiveEventPromotion_returnsActivePromotions() {
        promotion.setType(PromotionType.EVENT);
        promotion.setStatus(PromotionStatus.ACTIVE);
        Promotion secondPromotion = new Promotion();
        secondPromotion.setId(2L);
        secondPromotion.setType(PromotionType.EVENT);
        secondPromotion.setStatus(PromotionStatus.ACTIVE);
        List<Promotion> mockPromotions = List.of(promotion, secondPromotion);

        when(promotionRepository.findAllByTypeAndStatus(PromotionType.EVENT, PromotionStatus.ACTIVE))
                .thenReturn(mockPromotions);

        List<Promotion> result = promotionService.getAllActiveEventPromotion();

        assertNotNull(result);
        assertEquals(mockPromotions.size(), result.size());
        assertEquals(PromotionStatus.ACTIVE, result.get(0).getStatus());
        assertEquals(PromotionType.EVENT, result.get(0).getType());
        verify(promotionRepository).findAllByTypeAndStatus(PromotionType.EVENT, PromotionStatus.ACTIVE);
    }
}
