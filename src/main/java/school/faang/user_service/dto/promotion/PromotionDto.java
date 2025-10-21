package school.faang.user_service.dto.promotion;

import school.faang.user_service.entity.promotion.Rate;

public record PromotionDto(
        Long userId,
        Rate rate,
        Integer numberOfImpressions
) {
}
