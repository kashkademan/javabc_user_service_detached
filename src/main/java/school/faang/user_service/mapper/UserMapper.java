package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToDto(User user);

    default List<UserDto> toEventResponses(List<User> users) {
        return users.stream()
                .map(this::userToDto)
                .toList();
    }
}
