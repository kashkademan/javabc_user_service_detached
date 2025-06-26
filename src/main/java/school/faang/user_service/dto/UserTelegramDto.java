package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTelegramDto {
    private Long id;

    @NotNull(message = "Telegram user name must be provided")
    private String telegramUserName;
    private Long telegramChatId;
}
