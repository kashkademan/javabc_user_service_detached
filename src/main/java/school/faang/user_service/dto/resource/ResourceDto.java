package school.faang.user_service.dto.resource;

import lombok.Builder;
import lombok.experimental.FieldNameConstants;
import school.faang.user_service.entity.resource.ResourceStatus;
import school.faang.user_service.entity.resource.ResourceType;

import java.math.BigInteger;

@FieldNameConstants
@Builder
public record ResourceDto(
        Long id,
        String name,
        BigInteger size,
        ResourceType type,
        ResourceStatus status
) {
}