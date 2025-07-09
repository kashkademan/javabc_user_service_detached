package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.contact.RegisterTelegramDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.exception.ContactNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.utils.Utils;

@Service
@RequiredArgsConstructor
public class ContactService {
    public static final String USER_NOT_FOUND_BY_PHONE = "User by phone=[{}] is not found";
    public static final String CONTACT_BY_TYPE_NOT_FOUND = "Contact not found. Finding params: contact: {}, type: {}";

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final Utils utils;

    @Transactional
    public void registerTelegramChatId(RegisterTelegramDto registerTelegram) {
        User user = userRepository.findByPhone(registerTelegram.phone())
            .orElseThrow(() ->
                new UserNotFoundException(utils.format(USER_NOT_FOUND_BY_PHONE, registerTelegram.phone())));

        Contact contact = contactRepository.findByUserIdAndType(user.getId(), ContactType.TELEGRAM)
            .orElseGet(() -> Contact.builder()
                .user(user)
                .type(ContactType.TELEGRAM)
                .build()
            );
        contact.setContact(registerTelegram.chatId());
        contactRepository.save(contact);
    }

    @Transactional
    public void unregisterTelegramChatId(String chatId) {
        Contact contact = contactRepository.findByContactAndType(chatId, ContactType.TELEGRAM)
            .orElseThrow(() -> {
                String errorMessage = utils.format(CONTACT_BY_TYPE_NOT_FOUND, chatId, ContactType.TELEGRAM);
                return new ContactNotFoundException(errorMessage);
            });
        contactRepository.delete(contact);
    }
}
