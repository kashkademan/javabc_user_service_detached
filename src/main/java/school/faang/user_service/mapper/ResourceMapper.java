package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.resource.ResourceDto;
import school.faang.user_service.entity.resource.Resource;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ResourceMapper {

    ResourceDto toResourceDto(Resource resource);
}