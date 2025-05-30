package school.faang.user_service.mapper.promotion;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.model.redis.promotion.PromotionRedisModel;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PromotionMapper {
    Promotion toPromotionEntity(final PromotionCreateRequestDto promotionCreateRequestDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "tariff.id", target = "tariffId")
    PromotionResponseDto toPromotionResponseDto(final Promotion promotion);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "tariff.id", target = "tariffId")
    PromotionRedisModel toEventPromotionRedis(final Promotion promotion);
}
