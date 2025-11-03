package school.faang.user_service.controller.facade.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionCreateDto;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.service.promotion.PromotionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PromotionFacade {

    private final PromotionService promotionService;
    private final PromotionMapper promotionMapper;

    public PromotionDto createPromotion(PromotionCreateDto promotionCreateDto) {
        Promotion promotion = promotionMapper.toPromotion(promotionCreateDto);
        Promotion result = promotionService.create(promotion, promotionCreateDto.paymentRequest());
        return promotionMapper.toPromotionDto(result);
    }

    public List<PromotionDto> getPromotionByUserId() {
        List<Promotion> result = promotionService.getPromotionByUserId();
        return result.stream()
                .map(promotionMapper::toPromotionDto)
                .toList();
    }


}
