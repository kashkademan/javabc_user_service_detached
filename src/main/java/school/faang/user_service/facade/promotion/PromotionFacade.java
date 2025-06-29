package school.faang.user_service.facade.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.promotion.PromotionEventCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.dto.promotion.PromotionUserCreateRequestDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.mapper.promotion.PromotionEntityMapper;
import school.faang.user_service.service.promotion.PromotionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionFacade {
    private final PromotionService promotionService;
    private final PromotionEntityMapper promotionEntityMapper;

    public PromotionResponseDto createPromotionForEvent(PromotionEventCreateRequestDto promotionEventCreateRequestDto) {
        Promotion promotion = promotionService.createPromotionForEvent(
                promotionEventCreateRequestDto.getEventId(),
                promotionEventCreateRequestDto.getTariffId());

        PromotionResponseDto promotionResponseDto = promotionEntityMapper.toPromotionResponseDto(promotion);
        log.debug("Mapping Promotion entity to PromotionResponseDto. Entity content: {}. DTO content: {}.",
                promotion, promotionResponseDto);
        return promotionResponseDto;
    }

    public PromotionResponseDto createPromotionForUser(PromotionUserCreateRequestDto promotionUserCreateRequestDto) {
        Promotion promotion = promotionService.createPromotionForUser(
                promotionUserCreateRequestDto.getUserId(),
                promotionUserCreateRequestDto.getTariffId());

        PromotionResponseDto promotionResponseDto = promotionEntityMapper.toPromotionResponseDto(promotion);
        log.debug("Mapping Promotion entity to PromotionResponseDto. Entity content: {}. DTO content: {}.",
                promotion, promotionResponseDto);
        return promotionResponseDto;
    }
}
