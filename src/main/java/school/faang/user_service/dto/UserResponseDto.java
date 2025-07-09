package school.faang.user_service.dto;

import lombok.Builder;
import school.faang.user_service.entity.contact.PreferredContact;

@Builder
public record UserResponseDto(
    Long id,
    String username,
    String phone,
    String email,
    String chatId,
    String locale,
    PreferredContact preference
) {
}

