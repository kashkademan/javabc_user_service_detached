package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;

import java.util.Locale;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.WARN)
public interface MentorshipRequestMapper {
    MentorshipRequestDto toMentorshipRequestDto(MentorshipRequest mentorshipRequest);

    default String map(Locale locale) {
        return locale == null ? null : locale.toLanguageTag();
    }

    default Locale map(String localeTag) {
        return localeTag == null ? null : Locale.forLanguageTag(localeTag);
    }

    @Mapping(target = "locale", expression = "java(map(user.getLocale()))")
    @Mapping(target = "preference", expression = "java(mapPreference(user.getContactPreference()))")
    UserDto toUserDto(User user);

    default String mapPreference(ContactPreference contactPreference) {
        if (contactPreference == null || contactPreference.getPreference() == null) {
            return null;
        }
        return contactPreference.getPreference().name();
    }

}