package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.user.User;

import java.util.Locale;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "locale", expression = "java(toLocale(userDto.locale()))")
    User toUser(CreateUserDto userDto);

    @Mapping(target = "locale", expression = "java(toLocale(userDto.locale()))")
    void update(UpdateUserDto userDto, @MappingTarget User entity);

    @Mapping(target = "preference", expression = "java(fromPreference(user.getContactPreference()))")
    @Mapping(target = "locale", expression = "java(fromLocale(user.getLocale()))")
    UserDto toUserDto(User user);

    default Locale toLocale(String locale) {
        if (locale == null || locale.isBlank()) {
            return null;
        }
        return Locale.forLanguageTag(locale);
    }

    default String fromLocale(Locale locale) {
        if (locale == null) {
            return null;
        }
        return locale.toLanguageTag();
    }

    default String fromPreference(ContactPreference cp) {
        if (cp == null || cp.getPreference() == null) {
            return null;
        }
        return cp.getPreference().name();
    }
}
