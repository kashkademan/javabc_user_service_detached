package school.faang.user_service.dto.user;

import school.faang.user_service.entity.contact.ContactType;

public record ContactDto(
        String contact,
        ContactType type
) {
}
