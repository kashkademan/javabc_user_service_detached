package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(CreateUserDto userDto);

    void update(UpdateUserDto userDto, @MappingTarget User entity);

    @Mapping(target = "contactPreference", expression = "java(getPreferredContact(user))")
    UserDto toUserDto(User user);

    default PreferredContact getPreferredContact(User user) {
        return user.getContactPreference() != null ? user.getContactPreference().getPreference() : null;
    }
}
