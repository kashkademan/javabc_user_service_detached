package school.faang.user_service.dto.promotion;

import school.faang.user_service.entity.promotion.Tarif;

public record PromotionDto(
        Long userId,
        Tarif tarif,
        Integer numberOfDisplay
) {
}
