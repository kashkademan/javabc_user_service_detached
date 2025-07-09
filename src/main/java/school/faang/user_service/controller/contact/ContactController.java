package school.faang.user_service.controller.contact;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.contact.RegisterTelegramDto;
import school.faang.user_service.service.ContactService;

@Slf4j
@RestController
@Tag(name = "User contact service API", description = "API for user contacts")
@RequiredArgsConstructor
@RequestMapping("/contacts")
public class ContactController {
    private final ContactService contactService;

    @PostMapping("/telegram")
    @ResponseStatus(HttpStatus.CREATED)
    void registerTelegramChatId(@Valid @RequestBody RegisterTelegramDto registerTelegram) {
        log.debug("telegram chat registration request for the user. {}", registerTelegram);
        contactService.registerTelegramChatId(registerTelegram);
    }

    @DeleteMapping("/telegram/{chatId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void unregisterTelegramChatId(@PathVariable String chatId) {
        log.debug("request to cancel registration of telegram chat for the user. chatId: [{}]", chatId);
        contactService.unregisterTelegramChatId(chatId);
    }
}
