package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.config.redis.RedisLockPromotionProperties;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
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
    @Mock
    private ThreadPoolTaskExecutor executor;
    @Mock
    private RedisLockPromotionProperties props;
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
        assertEquals(RedisHashType.PROMOTION + ":" + promotion.getId(), capturedModel.getKey());
        assertEquals(promotion.getId(), capturedModel.getId());
    }

    @Test
    void shouldDecrementCountViewWhenPromotionFound() {
        // given
        long eventId = 123L;
        String key = "promo:123";
        PromotionRedisModel promo = new PromotionRedisModel();
        promo.setKey(key);
        promo.setCountView(10);

        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);

        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.of(promo));
        when(promotionRedisRepository.findById(key)).thenReturn(Optional.of(promo));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(valueOperations.setIfAbsent(anyString(), any(), any())).thenReturn(true);
        when(valueOperations.get(anyString())).thenReturn("lock-value");

        when(props.getExpireTime()).thenReturn(5000L);
        when(props.getMaxRetries()).thenReturn(1);
        when(props.getRetryDelay()).thenReturn(0);

        // when
        promotionRedisService.decrementCountViewByEventIds(List.of(eventId));

        // then
        verify(promotionRedisRepository).save(argThat(saved ->
                saved.getCountView() == 9
        ));
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
    public void shouldDecrementCountViewWhenPromotionFound() {
        // given
        long eventId = 123L;
        String key = "promo:123";
        PromotionRedisModel promo = new PromotionRedisModel();
        promo.setKey(key);
        promo.setCountView(10);

        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.of(promo));
        when(promotionRedisRepository.findById(key)).thenReturn(Optional.of(promo));
        when(redisTemplate.opsForValue().setIfAbsent(anyString(), any(), any())).thenReturn(true);
        when(redisTemplate.opsForValue().get(anyString())).thenReturn("lock-value");

        when(props.getExpireTime()).thenReturn(5000L);
        when(props.getMaxRetries()).thenReturn(1);
        when(props.getRetryDelay()).thenReturn(0);

        // when
        promotionRedisService.decrementCountViewByEventIds(List.of(eventId));

        // then
        // немедленное выполнение т.к. executor.execute(...) не запускает в реальности (можно использовать direct executor или верифицировать сохранение)
        verify(promotionRedisRepository).save(argThat(saved ->
                saved.getCountView() == 9
        ));
    }

    @Test
    void shouldSkipWhenPromotionNotFoundByEventId() {
        long eventId = 999L;

//        when(promotionRedisRepository.findByEventId(eventId)).thenReturn(Optional.empty());

        promotionRedisService.decrementCountViewByEventIds(List.of(eventId));

        verify(promotionRedisRepository, never()).save(any());
    }
}