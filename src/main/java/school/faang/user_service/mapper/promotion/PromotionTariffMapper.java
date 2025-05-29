package school.faang.user_service.mapper.promotion;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionTariffResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.model.redis.promotion.PromotionTariffRedis;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PromotionTariffMapper {
    PromotionTariffResponseDto toPromotionTariffResponseDto(final PromotionTariff promotionTariff);

    List<PromotionTariffResponseDto> toPromotionTariffResponseDtoList(final List<PromotionTariff> promotionTariffs);

    PromotionTariffRedis toPromotionTariffRedis(final PromotionTariff promotionTariff);
}
