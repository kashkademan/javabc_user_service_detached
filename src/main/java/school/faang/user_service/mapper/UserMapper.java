package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(UserCreateDto userDto);

    void update(UserUpdateDto userDto, @MappingTarget User entity);

    User clone(User source);

    @Mapping(source = "avatarUrl", target = "avatarUrl")
    UserDto toUserDto(User user);
}
