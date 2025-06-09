package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapperImpl;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionRedisServiceTest {
    @Mock
    private PromotionRedisRepository promotionRedisRepository;
    @Spy
    private PromotionRedisMapperImpl promotionRedisMapper;
    @Captor
    private ArgumentCaptor<PromotionRedisModel> promotionRedisModelCaptor;
    @InjectMocks
    private PromotionRedisService promotionRedisService;

    private Promotion promotion;

    @BeforeEach
    public void setUp() {
        promotion = new Promotion();
        promotion.setId(13L);
    }

    @Test
    void testSavePromotion_successfully() {
        assertDoesNotThrow(() -> promotionRedisService.savePromotion(promotion));

        verify(promotionRedisRepository).save(promotionRedisModelCaptor.capture());

        PromotionRedisModel capturedModel = promotionRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(RedisHashType.PROMOTION + ": " + promotion.getId(), capturedModel.getKey());
        assertEquals(promotion.getId(), capturedModel.getId());
    }

    @Test
    void testGetAllPromotions_returnList() {
        PromotionRedisModel p1 = new PromotionRedisModel();
        PromotionRedisModel p2 = new PromotionRedisModel();

        Iterable<PromotionRedisModel> iterable = List.of(p1, p2);
        when(promotionRedisRepository.findAll()).thenReturn(iterable);

        List<PromotionRedisModel> result = promotionRedisService.getAllPromotions();

        assertEquals(2, result.size());
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p2));
    }

    @Test
    void testDecrementCountViewByEventIds_decrementAndSave() {
        Long eventId = 42L;

        PromotionRedisModel model = new PromotionRedisModel();
        model.setKey("PROMOTION: 42");
        model.setCountView(5);

        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.of(model));
        when(promotionRedisRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        promotionRedisService.decrementCountViewByEventIds(List.of(eventId));

        assertEquals(4, model.getCountView());
        verify(promotionRedisRepository).save(model);
    }

    @Test
    void testDecrementCountViewByEventIds_doNothingIfNotFound() {
        Long eventId = 42L;
        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> promotionRedisService.decrementCountViewByEventIds(List.of(eventId)));

        verify(promotionRedisRepository).findByEventId(eventId);
        verify(promotionRedisRepository, never()).save(any());
    }

    @Test
    void testDeletePromotionByKey_successfully() {
        String key = "PROMOTION: 42";

        assertDoesNotThrow(() -> promotionRedisService.deletePromotionByKey(key));

        verify(promotionRedisRepository).deleteById(key);
    }
}