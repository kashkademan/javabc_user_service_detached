package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(CreateUserDto userDto);

    void update(UpdateUserDto userDto, @MappingTarget User entity);

    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(target = "followersIds", expression = "java(mapFollowers(user))")
    UserDto toUserDto(User user);

    List<UserDto> toUserDtos(List<User> users);

    default List<Long> mapFollowers(User user) {
        return user.getFollowers().stream()
                .map(User::getId)
                .toList();
    }
}
