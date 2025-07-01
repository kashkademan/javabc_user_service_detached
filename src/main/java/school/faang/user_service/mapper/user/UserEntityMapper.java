package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserNotificationResponseDto;
import school.faang.user_service.dto.user.UserRegisterRequestDto;
import school.faang.user_service.dto.user.UserRegisterResponseDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserEntityMapper {
    User toUserEntity(UserRegisterRequestDto userRegisterRequestDto);

    UserRegisterResponseDto toUserRegisterResponseDto(User user);

    UserResponseDto toUserLiteResponseDto(User user);

    List<UserResponseDto> toUserLiteResponseDtoList(List<User> users);

    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(source = "country.locale", target = "locale")
    UserNotificationResponseDto toUserNotificationResponseDto(User user);

    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(source = "country.locale", target = "locale")
    List<UserNotificationResponseDto> toUserNotificationResponseDtoList(List<User> users);

    List<UserResponseDto> toUserResponseDtoList(List<User> users);
}
