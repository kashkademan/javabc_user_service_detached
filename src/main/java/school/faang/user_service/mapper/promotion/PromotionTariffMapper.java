package school.faang.user_service.mapper.promotion;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionTariffResponseDto;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PromotionTariffMapper {
    PromotionTariffResponseDto toPromotionTariffResponseDto(PromotionTariff promotionTariff);

    List<PromotionTariffResponseDto> toPromotionTariffResponseDtoList(List<PromotionTariff> promotionTariffs);
}
