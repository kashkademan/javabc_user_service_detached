package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    List<UserResponseDto> toUserResponseDtoList(List<User> users);
}
