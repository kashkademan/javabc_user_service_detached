package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.messaging.events.PremiumBoughtEvent;

@Mapper(componentModel = "Spring")
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toPremiumDto(Premium premium);

    @Mapping(source = "user.id", target = "userId")
    PremiumBoughtEvent toBoughtEvent(Premium premium);
}