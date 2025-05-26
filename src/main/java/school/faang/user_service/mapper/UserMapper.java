package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.CreateUserDto;
import school.faang.user_service.dto.NotificationUserDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userProfilePic.fileId", source = "fileId")
    User toEntity(UserDto dto);

    @Mapping(target = "fileId", source = "userProfilePic.fileId")
    UserDto toDto(User user);

    @Mapping(target = "preference", source = "contactPreference.preference")
    NotificationUserDto toNotificationUserDto (User user);

    User toEntity(CreateUserDto createUserDto);

    @Mapping(target = "fileId", source = "userProfilePic.fileId")
    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "userProfilePic.fileId", source = "fileId")
    List<User> toEntityList(List<UserDto> userDtos);
}
