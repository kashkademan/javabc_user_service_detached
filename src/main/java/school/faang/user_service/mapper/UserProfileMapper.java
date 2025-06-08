package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserProfileDto;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "presignedUrl", source = "presignedUrl")
    UserProfileDto toUserProfileDto(String presignedUrl);
}
