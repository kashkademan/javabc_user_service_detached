package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(UserDto uSerDto);
    List<UserDto> mapListOfUsers(List<User> subscriptions);

    @Mapping(target = "pictureSmallFileId", source = "userProfilePic.smallFileId")
    @Mapping(target = "pictureFileId", source = "userProfilePic.fileId")
    UserPersonalDto toUserPersonalDto(User user);
}