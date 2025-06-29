package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    UserDto toDto(User user);

    @Mapping(target = "id", source = "id")
    User toEntity(UserDto userDto);

    List<UserDto> toDtos(List<User> users);
}
