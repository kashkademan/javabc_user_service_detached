package school.faang.user_service.dto.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserDto> mapListOfUsers(List<User> subscriptions);
}
