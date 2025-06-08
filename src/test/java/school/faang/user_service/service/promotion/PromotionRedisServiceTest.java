package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapper;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PromotionRedisServiceTest {
    @Mock
    private PromotionRedisRepository promotionRedisRepository;
    @Spy
    private PromotionRedisMapper promotionRedisMapper;
    @Captor
    private ArgumentCaptor<PromotionRedisModel> promotionRedisModelCaptor;
    @InjectMocks
    private PromotionRedisService promotionRedisService;

    @Test
    void testSavePromotion_savePromotionAndEvent() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);

//        PromotionRedisModel mappedModel = new PromotionRedisModel();
//        mappedModel.setId(1L); // Укажи нужные поля
//        doReturn(mappedModel).when(promotionRedisMapper).toEventPromotionRedis(promotion);

        // when
        assertDoesNotThrow(() -> promotionRedisService.savePromotion(promotion));

        // then
        verify(promotionRedisRepository).save(promotionRedisModelCaptor.capture());

        PromotionRedisModel capturedModel = promotionRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals("PROMOTION: 1", capturedModel.getKey());
        assertEquals(1L, capturedModel.getId());
    }

//    @Test
//    void testUpdatePromotedEvent_saveUpdatedEvent() {
//        Event event = new Event();
//        EventRedisModel eventRedisModel = new EventRedisModel();
//
//        when(eventRedisMapper.toEventRedis(event)).thenReturn(eventRedisModel);
//        when(eventRedisRepository.save(any())).thenReturn(eventRedisModel);
//
//        promotionRedisService.updatePromotedEvent(event);
//
//        verify(eventRedisRepository).save(eventRedisModel);
//    }
//
//    @Test
//    void testGetPromotedEvents_returnFilteredEventsAndDecrementViews() {
//        EventRedisModel model = new EventRedisModel();
//        model.setId("e1");
//        model.setPromotionId("p1");
//        model.setTitle("Test");
//        model.setStartDate(LocalDateTime.now().plusHours(1));
//        model.setCoefficientPriority(1);
//
//        PromotionRedisModel promoModel = new PromotionRedisModel();
//        promoModel.setId("p1");
//        promoModel.setCountView(5);
//
//        when(eventRedisRepository.findAll()).thenReturn(List.of(model));
//        when(promotionRedisRepository.findById("p1")).thenReturn(Optional.of(promoModel));
//        when(promotionRedisRepository.save(any())).thenReturn(promoModel);
//        when(eventRedisMapper.toEventEntity(model)).thenReturn(new Event());
//
//        EventFilter filter = new EventFilter();
//        filter.setTitle("Test");
//
//        List<Event> result = promotionRedisService.getPromotedEvents(filter);
//
//        assertEquals(1, result.size());
//        verify(promotionRedisRepository).save(any());
//    }
//
//    @Test
//    void testDecrementCountView_deletePromotionAndEventWhenViewZero() {
//        EventRedisModel eventRedis = new EventRedisModel();
//        eventRedis.setId("event1");
//        eventRedis.setPromotionId("promo1");
//
//        PromotionRedisModel promo = new PromotionRedisModel();
//        promo.setId(eventRedis.getPromotionId());
//        promo.setCountView(1);
//
//        when(promotionRedisRepository.findById(eventRedis.getPromotionId())).thenReturn(Optional.of(promo));
//
//        EventFilter filter = new EventFilter();
//        when(eventRedisRepository.findAll()).thenReturn(List.of(eventRedis));
//        when(eventRedisMapper.toEventEntity(any())).thenReturn(new Event());
//
//        promotionRedisService.getPromotedEvents(filter);
//
//        verify(promotionViewExpiredQueueStorage).addDeletedPromotion(promo.getId());
//        verify(eventRedisRepository).deleteById(eventRedis.getId());
//        verify(promotionRedisRepository).deleteById(promo.getId());
//    }
//
//    @Test
//    void testDecrementCountView_promotionNotFound() {
//        EventRedisModel model = new EventRedisModel();
//        model.setId("eventX");
//        model.setPromotionId("promoX");
//
//        when(eventRedisRepository.findAll()).thenReturn(List.of(model));
//        when(promotionRedisRepository.findById(model.getPromotionId())).thenReturn(Optional.empty());
//
//        EventFilter filter = new EventFilter();
//        when(eventRedisMapper.toEventEntity(any())).thenReturn(new Event());
//
//        promotionRedisService.getPromotedEvents(filter);
//
//        verify(eventRedisRepository).deleteById(model.getId());
//        verify(promotionViewExpiredQueueStorage).addDeletedPromotion(model.getPromotionId());
//    }
}