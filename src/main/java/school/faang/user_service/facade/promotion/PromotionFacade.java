package school.faang.user_service.facade.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionMapper;
import school.faang.user_service.service.promotion.PromotionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionFacade {
    private final PromotionService promotionService;
    private final PromotionMapper promotionMapper;

    public PromotionResponseDto createPromotion(final PromotionCreateRequestDto promotionCreateRequestDto) {
        // TODO: подумать нужен ли маппинг
//        Promotion promotion = promotionMapper.toPromotionEntity(promotionCreateRequestDto);
//        log.debug("Mapping PromotionCreateRequestDto to Promotion entity.DTO content: {}. Entity content: {}.",
//                promotionCreateRequestDto, promotion);

        Promotion promotion = promotionService.createPromotion(
                promotionCreateRequestDto.getEventId(),
                promotionCreateRequestDto.getTariffId());

        PromotionResponseDto promotionResponseDto = promotionMapper.toPromotionResponseDto(promotion);
        log.debug("Mapping Promotion entity to PromotionResponseDto. Entity content: {}. DTO content: {}.",
                promotion, promotionResponseDto);
        return promotionResponseDto;
    }
}
