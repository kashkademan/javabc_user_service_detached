package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.contact.RegisterTelegramDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.exception.ContactNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.contact.ContactRepository;
import school.faang.user_service.utils.Utils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ContactServiceTest {
    public static final String CHAT_ID = "1";
    public static final String PHONE = "1234567890";
    public static final Long CONTACT_ID = 100L;
    public static final Long USER_ID = 10L;
    public static final String USER_NAME = "Simple-User-Name";

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private Utils utils;
    @InjectMocks
    private ContactService contactService;

    @Test
    public void testRegisterTelegramWhenContactIsEmpty() {
        final RegisterTelegramDto registerTelegram = getMockRegisterTelegramDto();
        final User user = getMockUser();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.of(user));
        when(contactRepository.findByUserIdAndType(USER_ID, ContactType.TELEGRAM)).thenReturn(Optional.empty());
        when(contactRepository.save(any())).thenReturn(any());

        contactService.registerTelegramChatId(registerTelegram);

        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    public void testRegisterTelegramWhenContactIsExists() {
        final RegisterTelegramDto registerTelegram = getMockRegisterTelegramDto();
        final User user = getMockUser();
        final Contact contact = getMockContact();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.of(user));
        when(contactRepository.findByUserIdAndType(USER_ID, ContactType.TELEGRAM)).thenReturn(Optional.of(contact));
        when(contactRepository.save(any())).thenReturn(any());

        contactService.registerTelegramChatId(registerTelegram);

        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    public void testFailRegisterTelegram_PhoneIsMissing() {
        final RegisterTelegramDto registerTelegram = getMockRegisterTelegramDto();
        final String expecterErrorMessage = utils.format(ContactService.USER_NOT_FOUND_BY_PHONE, PHONE);

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.empty());

        UserNotFoundException actualException = assertThrows(
            UserNotFoundException.class,
            () -> contactService.registerTelegramChatId(registerTelegram)
        );

        verify(contactRepository, times(0)).save(any(Contact.class));
        assertEquals(expecterErrorMessage, actualException.getMessage());
    }

    @Test
    public void testUnregisterTelegramChat() {
        Contact contact = getMockContact();

        when(contactRepository.findByContactAndType(CHAT_ID, ContactType.TELEGRAM))
            .thenReturn(Optional.of(contact));

        contactService.unregisterTelegramChatId(CHAT_ID);

        verify(contactRepository).delete(any(Contact.class));
    }

    @Test
    public void testFailUnregisterTelegramChat_ChatMissing() {
        final String expectedMessage = utils.format(
            ContactService.CONTACT_BY_TYPE_NOT_FOUND, CHAT_ID, ContactType.TELEGRAM);

        when(contactRepository.findByContactAndType(CHAT_ID, ContactType.TELEGRAM))
            .thenReturn(Optional.empty());

        ContactNotFoundException actualException = assertThrows(
            ContactNotFoundException.class,
            () -> contactService.unregisterTelegramChatId(CHAT_ID)
        );

        verify(contactRepository, times(0)).delete(any(Contact.class));
        assertEquals(expectedMessage, actualException.getMessage());
    }

    private static RegisterTelegramDto getMockRegisterTelegramDto() {
        return RegisterTelegramDto.builder()
            .chatId(CHAT_ID)
            .phone(PHONE)
            .build();
    }

    private User getMockUser() {
        return User.builder()
            .id(USER_ID)
            .username(USER_NAME)
            .build();
    }

    private Contact getMockContact() {
        return Contact.builder()
            .id(CONTACT_ID)
            .contact(CHAT_ID)
            .type(ContactType.TELEGRAM)
            .user(getMockUser())
            .build();
    }
}