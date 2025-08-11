package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.subscription.FolloweeSumDto;
import school.faang.user_service.dto.subscription.FollowerIdDto;
import school.faang.user_service.entity.FolloweeSumProjection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {
    FolloweeSumDto toDto(FolloweeSumProjection followeeSumProjection);

    default FollowerIdDto toFollowerIdDto(Long followerId) {
        return new FollowerIdDto(followerId);
    }
}
