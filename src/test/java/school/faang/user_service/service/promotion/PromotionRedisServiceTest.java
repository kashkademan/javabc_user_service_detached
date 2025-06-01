package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.event.EventRedisMapper;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.model.redis.event.EventRedisModel;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;
import school.faang.user_service.storage.promotion.PromotionViewExpiredQueueStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionRedisServiceTest {

    @Mock
    private EventRedisRepository eventRedisRepository;
    @Mock
    private PromotionRedisRepository promotionRedisRepository;
    @Mock
    private PromotionViewExpiredQueueStorage promotionViewExpiredQueueStorage;
    @Mock
    private PromotionMapper promotionMapper;
    @Mock
    private EventRedisMapper eventRedisMapper;

    @InjectMocks
    private PromotionRedisService promotionRedisService;

    @Test
    void testSavePromotion_savePromotionAndEvent() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setEndDate(LocalDateTime.now().plusDays(1));

        Event event = new Event();
        EventRedisModel eventRedisModel = new EventRedisModel();
        PromotionRedisModel promotionRedisModel = new PromotionRedisModel();

        when(promotionMapper.toEventPromotionRedis(promotion)).thenReturn(promotionRedisModel);
        when(eventRedisMapper.toEventRedis(event)).thenReturn(eventRedisModel);
        when(eventRedisRepository.save(any())).thenReturn(eventRedisModel);
        when(promotionRedisRepository.save(any())).thenReturn(promotionRedisModel);

        promotionRedisService.savePromotion(promotion, event);

        verify(eventRedisRepository).save(eventRedisModel);
        verify(promotionRedisRepository).save(promotionRedisModel);
    }

    @Test
    void testUpdatePromotedEvent_saveUpdatedEvent() {
        Event event = new Event();
        EventRedisModel eventRedisModel = new EventRedisModel();

        when(eventRedisMapper.toEventRedis(event)).thenReturn(eventRedisModel);
        when(eventRedisRepository.save(any())).thenReturn(eventRedisModel);

        promotionRedisService.updatePromotedEvent(event);

        verify(eventRedisRepository).save(eventRedisModel);
    }

    @Test
    void testGetPromotedEvents_returnFilteredEventsAndDecrementViews() {
        EventRedisModel model = new EventRedisModel();
        model.setId("e1");
        model.setPromotionId("p1");
        model.setTitle("Test");
        model.setStartDate(LocalDateTime.now().plusHours(1));
        model.setCoefficientPriority(1);

        PromotionRedisModel promoModel = new PromotionRedisModel();
        promoModel.setId("p1");
        promoModel.setCountView(5);

        when(eventRedisRepository.findAll()).thenReturn(List.of(model));
        when(promotionRedisRepository.findById("p1")).thenReturn(Optional.of(promoModel));
        when(promotionRedisRepository.save(any())).thenReturn(promoModel);
        when(eventRedisMapper.toEventEntity(model)).thenReturn(new Event());

        EventFilter filter = new EventFilter();
        filter.setTitle("Test");

        List<Event> result = promotionRedisService.getPromotedEvents(filter);

        assertEquals(1, result.size());
        verify(promotionRedisRepository).save(any());
    }

    @Test
    void testDecrementCountView_deletePromotionAndEventWhenViewZero() {
        EventRedisModel eventRedis = new EventRedisModel();
        eventRedis.setId("event1");
        eventRedis.setPromotionId("promo1");

        PromotionRedisModel promo = new PromotionRedisModel();
        promo.setId(eventRedis.getPromotionId());
        promo.setCountView(1);

        when(promotionRedisRepository.findById(eventRedis.getPromotionId())).thenReturn(Optional.of(promo));

        EventFilter filter = new EventFilter();
        when(eventRedisRepository.findAll()).thenReturn(List.of(eventRedis));
        when(eventRedisMapper.toEventEntity(any())).thenReturn(new Event());

        promotionRedisService.getPromotedEvents(filter);

        verify(promotionViewExpiredQueueStorage).addDeletedPromotion(promo.getId());
        verify(eventRedisRepository).deleteById(eventRedis.getId());
        verify(promotionRedisRepository).deleteById(promo.getId());
    }

    @Test
    void testDecrementCountView_promotionNotFound() {
        EventRedisModel model = new EventRedisModel();
        model.setId("eventX");
        model.setPromotionId("promoX");

        when(eventRedisRepository.findAll()).thenReturn(List.of(model));
        when(promotionRedisRepository.findById(model.getPromotionId())).thenReturn(Optional.empty());

        EventFilter filter = new EventFilter();
        when(eventRedisMapper.toEventEntity(any())).thenReturn(new Event());

        promotionRedisService.getPromotedEvents(filter);

        verify(eventRedisRepository).deleteById(model.getId());
        verify(promotionViewExpiredQueueStorage).addDeletedPromotion(model.getPromotionId());
    }
}