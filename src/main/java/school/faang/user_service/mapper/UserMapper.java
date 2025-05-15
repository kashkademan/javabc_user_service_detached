package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    UserDto toUserDTO(User user);
}
