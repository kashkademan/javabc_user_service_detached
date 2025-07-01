package school.faang.user_service.mapper.user;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserFilterRequestDto;
import school.faang.user_service.model.user.UserFilter;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface UserFilterMapper {
    UserFilter toFilter(UserFilterRequestDto userFilterRequestDto);
}
