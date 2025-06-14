package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import school.faang.user_service.config.redis.RedisLockPromotionProperties;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionRedisMapperImpl;
import school.faang.user_service.model.redis.RedisHashType;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;
import school.faang.user_service.repository.promotion.PromotionRedisRepository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionRedisServiceTest {
    @Mock
    private PromotionRedisRepository promotionRedisRepository;
    @Spy
    private PromotionRedisMapperImpl promotionRedisMapper;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    private Executor executor = new SyncTaskExecutor();
    @Mock
    private RedisLockPromotionProperties props;
    @Captor
    private ArgumentCaptor<PromotionRedisModel> promotionRedisModelCaptor;

    private PromotionRedisService promotionRedisService;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setId(14L);
        promotionRedisService = new PromotionRedisService(
                promotionRedisRepository,
                promotionRedisMapper,
                redisTemplate,
                executor,
                props);
    }

    @Test
    void testSavePromotion_successfully() {
        assertDoesNotThrow(() -> promotionRedisService.savePromotion(promotion));

        verify(promotionRedisRepository).save(promotionRedisModelCaptor.capture());

        PromotionRedisModel capturedModel = promotionRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(RedisHashType.PROMOTION + ":" + promotion.getId(), capturedModel.getKey());
        assertEquals(promotion.getId(), capturedModel.getId());
    }

    @Test
    void testDeletePromotionByKey_successfully() {
        String key = "PROMOTION:42";

        assertDoesNotThrow(() -> promotionRedisService.deletePromotionByKey(key));

        verify(promotionRedisRepository).deleteById(key);
    }

    @Test
    void testGetAllPromotions_returnListOfPromotions() {
        PromotionRedisModel model1 = new PromotionRedisModel();
        PromotionRedisModel model2 = new PromotionRedisModel();
        Iterable<PromotionRedisModel> iterable = List.of(model1, model2);

        when(promotionRedisRepository.findAll()).thenReturn(iterable);

        List<PromotionRedisModel> result = promotionRedisService.getAllPromotions();

        assertEquals(2, result.size());
        assertTrue(result.contains(model1));
        assertTrue(result.contains(model2));
        verify(promotionRedisRepository).findAll();
    }

    @Test
    void testDecrementCountViewByEventIds_promotionFound() {
        long eventId = 123L;
        PromotionRedisModel promotionRedisModel = new PromotionRedisModel();
        promotionRedisModel.setKey("PROMOTION:123");
        promotionRedisModel.setId(123L);
        promotionRedisModel.setCountView(10);
        promotionRedisModel.setEventId(eventId);

        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        when(promotionRedisRepository.findByEventId(eventId))
                .thenReturn(Optional.of(promotionRedisModel));
        when(promotionRedisRepository.findById(promotionRedisModel.getKey()))
                .thenReturn(Optional.of(promotionRedisModel));
        when(props.getExpireTime()).thenReturn(3000L);
        when(props.getMaxRetries()).thenReturn(10);
        when(props.getRetryDelay()).thenReturn(300);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), any(), any(Duration.class))).thenReturn(true);

        assertDoesNotThrow(() -> promotionRedisService.decrementCountViewByEventIds(List.of(eventId)));

        verify(promotionRedisRepository).save(any(PromotionRedisModel.class));
    }

    @Test
    void testDecrementCountViewByEventIds_promotionNotFound() {
        long eventId = 999L;

        promotionRedisService.decrementCountViewByEventIds(List.of(eventId));

        verify(promotionRedisRepository, never()).save(any());
    }
}