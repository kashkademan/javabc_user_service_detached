package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring")
public interface PremiumMapper {
    Premium toPremium(PremiumDto premiumDto);
    @Mapping(target = "userId", source = "user.id")
    PremiumDto toPremiumDto(Premium premium);
}
