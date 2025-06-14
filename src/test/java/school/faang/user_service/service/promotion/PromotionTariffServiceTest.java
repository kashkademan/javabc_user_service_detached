package school.faang.user_service.service.promotion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.exception.promotion.PromotionTariffNotFoundException;
import school.faang.user_service.repository.promotion.PromotionTariffRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PromotionTariffServiceTest {
    @Mock
    private PromotionTariffRepository promotionTariffRepository;
    @InjectMocks
    private PromotionTariffService promotionTariffService;
    private PromotionTariff promotionTariff;

    @BeforeEach
    public void setUp() {
        promotionTariff = new PromotionTariff();
        promotionTariff.setId(18L);
    }

    @Test
    public void testGetPromotionTariffById_successfully() {
        when(promotionTariffRepository.findById(promotionTariff.getId())).thenReturn(Optional.of(promotionTariff));

        PromotionTariff returnpromotionTariff = promotionTariffService.getPromotionTariffById(promotionTariff.getId());

        verify(promotionTariffRepository, times(1)).findById(promotionTariff.getId());
        assertEquals(promotionTariff.getId(), returnpromotionTariff.getId());
    }

    @Test
    public void testGetPromotionTariffById_promotionTariffNotFound() {
        when(promotionTariffRepository.findById(promotionTariff.getId())).thenReturn(Optional.empty());

        assertThrows(PromotionTariffNotFoundException.class, () -> promotionTariffService.getPromotionTariffById(promotionTariff.getId()));
        verify(promotionTariffRepository, times(1)).findById(promotionTariff.getId());
    }

    @Test
    void testGetAllActivePromotionTariff_shouldReturnNonDeletedTariffs() {
        PromotionTariff secondTariff = new PromotionTariff();
        secondTariff.setId(17L);
        List<PromotionTariff> tariffs = List.of(
                promotionTariff,
                secondTariff
        );

        when(promotionTariffRepository.findAllByDeletedFalse()).thenReturn(tariffs);

        List<PromotionTariff> result = promotionTariffService.getAllActivePromotionTariff();

        assertEquals(tariffs.size(), result.size());
        assertEquals(tariffs, result);
        verify(promotionTariffRepository).findAllByDeletedFalse();
    }
}
