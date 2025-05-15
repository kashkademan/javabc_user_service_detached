package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    UserDto toUserDto(User user);
    User toUser(UserDto uSerDto);
    List<UserDto> mapListOfUsers(List<User> subscriptions);

}

