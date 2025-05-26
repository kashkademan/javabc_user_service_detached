package school.faang.user_service.facade.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionTariffResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.mapper.promotion.PromotionTariffMapper;
import school.faang.user_service.service.promotion.PromotionTariffService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionTariffFacade {
    private final PromotionTariffService promotionTariffService;
    private final PromotionTariffMapper promotionTariffMapper;
    public List<PromotionTariffResponseDto> getAllActivePromotionTariff() {
        List<PromotionTariff> promotionTariffList = promotionTariffService.getAllActivePromotionTariff();

        List<PromotionTariffResponseDto> promotionTariffResponseDtoList =
                promotionTariffMapper.toPromotionTariffResponseDtoList(promotionTariffList);
        log.debug("Mapping Promotion Tariff entity list to PromotionTariffResponseDto list." +
                        "Entity content: {}. DTO content: {}.", promotionTariffList, promotionTariffResponseDtoList);
        return promotionTariffResponseDtoList;
    }
}
