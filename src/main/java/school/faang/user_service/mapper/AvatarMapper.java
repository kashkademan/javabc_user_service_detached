package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import school.faang.user_service.dto.avatar.AvatarDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AvatarMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "userProfilePic", target = "avatarUrl", qualifiedByName = "extractAvatarUrl")
    @Mapping(source = "username", target = "seed")
    AvatarDto toDto(User user);

    @Named("extractAvatarUrl")
    default String extractAvatarUrl(UserProfilePic userProfilePic) {
        return userProfilePic != null ? userProfilePic.getFileId() : null;
    }
}