package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "preference", source = "contactPreference.preference")
    UserDto userToDto(User user);

    User toUserEntity(UserRegisterRequestDto userRegisterRequestDto);

    UserRegisterResponseDto toUserRegisterResponseDto(User user);

    default List<UserDto> toEventResponses(List<User> users) {
        return users.stream()
                .map(this::userToDto)
                .toList();
    }
}
