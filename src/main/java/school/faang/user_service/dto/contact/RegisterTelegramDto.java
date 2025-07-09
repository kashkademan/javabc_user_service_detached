package school.faang.user_service.dto.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RegisterTelegramDto(
    @NotNull(message = "The chat Id cannot be empty")
    String chatId,
    @NotBlank(message = "The phone number cannot be empty")
    String phone
) {
}
