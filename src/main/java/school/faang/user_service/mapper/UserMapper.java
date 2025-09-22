package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(UserCreateDto userDto);

    void update(UserUpdateDto userDto, @MappingTarget User entity);

    User clone(User source);

    @Mapping(source = "avatarUrl", target = "avatarUrl")
    @Mapping(target = "followersIds", expression = "java(mapFollowersToIds(user))")
    UserDto toUserDto(User user);

    default List<Long> mapFollowersToIds(User user) {
        if (user == null || user.getFollowers() == null) {
            return new ArrayList<>();  //
        }
        return user.getFollowers().stream()
                .map(User::getId)
                .toList();
    }
}
