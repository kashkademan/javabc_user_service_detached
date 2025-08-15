package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(UserCreateDto userDto);

    void update(UserUpdateDto userDto, @MappingTarget User entity);

    @Mapping(target = "preference", expression = "java(user.getContactPreference() != null ? "
            + "user.getContactPreference().getPreference() : null)")
    UserViewDto toUserDto(User user);


}
