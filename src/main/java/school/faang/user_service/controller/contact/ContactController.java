package school.faang.user_service.controller.contact;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.contact.ContactPreferenceRequestDto;
import school.faang.user_service.dto.contact.ContactPreferenceResponseDto;
import school.faang.user_service.facade.contact.ContactFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contacts")
@Slf4j
public class ContactController {
    private final ContactFacade contactFacade;

    @GetMapping()
    public ResponseEntity<List<ContactPreferenceResponseDto>> getAllContact() {
        log.debug("Contact controller accepted request get all contact");

        List<ContactPreferenceResponseDto> response = contactFacade.getAllContact();
        log.debug("Contact controller return response get all contact {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ContactPreferenceResponseDto> setPreferenceContactForUser
            (@RequestBody @Valid ContactPreferenceRequestDto contactPreferenceRequestDto) {
        log.debug("User controller accepted request set preference contact for user {}", contactPreferenceRequestDto);

        ContactPreferenceResponseDto response = contactFacade.setPreferenceContactForUser(contactPreferenceRequestDto);
        log.debug("User controller return response set preference contact for user {}", response);
        return ResponseEntity.ok(response);
    }
}
