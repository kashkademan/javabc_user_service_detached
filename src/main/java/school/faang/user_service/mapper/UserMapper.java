package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.RegisterParticipantRequestDto;
import school.faang.user_service.dto.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    RegisterParticipantRequestDto toRegisterParticipantRequestDto(User user);

    List<RegisterParticipantRequestDto> toRegisterParticipantRequestDtoList(List<User> users);

    @Mapping(target = "chatId", expression = "java(getChatId(user))")
    @Mapping(target = "preference", expression = "java(getPreference(user))")
    @Mapping(target = "locale", constant = "ru")
    UserResponseDto toUserResponseDto(User user);

    @Named("getChartId")
    default String getChatId(User user) {
        List<Contact> contacts = user.getContacts();
        if (contacts == null) {
            return null;
        } else {
            return contacts.stream()
                .filter(contact -> contact.getType().equals(ContactType.TELEGRAM))
                .findFirst()
                .map(Contact::getContact)
                .orElse(null);
        }
    }

    @Named("getPreference")
    default PreferredContact getPreference(User user) {
        ContactPreference contactPreference = user.getContactPreference();
        return contactPreference == null ? null : contactPreference.getPreference();
    }
}
