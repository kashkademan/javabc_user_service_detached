package school.faang.user_service.facade.contact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.contact.ContactPreferenceRequestDto;
import school.faang.user_service.dto.contact.ContactPreferenceResponseDto;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.mapper.contact.ContactMapper;
import school.faang.user_service.service.contact.ContactService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactFacade {
    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public List<ContactPreferenceResponseDto> getAllContact() {
        List<ContactPreference> contactPreferences = contactService.getAllContact();

        List<ContactPreferenceResponseDto> contactPreferenceResponseDtoList =
                contactMapper.toContactPreferenceResponseDtoList(contactPreferences);

        log.debug("Contact preference entity list to ContactPreferenceResponseDto list." +
                        "Entity content: {}.DTO content: {}.",
                contactPreferences, contactPreferenceResponseDtoList);

        return contactPreferenceResponseDtoList;
    }

    public ContactPreferenceResponseDto setPreferenceContactForUser(ContactPreferenceRequestDto contactPreferenceRequestDto) {
        ContactPreference contactPreference = contactService.setPreferenceContactForUser(
                contactPreferenceRequestDto.getUserId(),
                contactPreferenceRequestDto.getPreference()
        );



        ContactPreferenceResponseDto contactPreferenceResponseDto =
                contactMapper.toContactPreferenceResponseDto(contactPreference);
        log.debug("Contact preference entity to ContactPreferenceResponseDto." +
                        "Entity content: {}.DTO content: {}.",
                contactPreference, contactPreferenceResponseDto);

        return contactPreferenceResponseDto;
    }
}
