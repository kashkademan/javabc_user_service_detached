package school.faang.user_service.validator.telegram;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserTelegramDto;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.exception.DataValidationException;

@Component
public class TelegramValidator implements school.faang.user_service.validator.TelegramValidator {
    @Override
    public void validateTelegramUserId(UserTelegramDto userTelegramDto, long userId) {
        if (userTelegramDto.getUserId() == null || userTelegramDto.getUserId() == userId) {
            return;
        }

        throw new DataValidationException(String.format(
                "Provided user id and existing user id for telegram user name \"%s\" must be the same",
                userTelegramDto.getTelegramUserName()));
    }

    @Override
    public void validateTelegramChatId(long providedChatId, Long chatId, long userId) {
        if (chatId == null || providedChatId == chatId) {
            return;
        }
        throw new DataValidationException(String.format("Chat id for user %d exists and different from presented", userId));
    }

    @Override
    public void validateTelegramPreference(UserDto userDto) {
        if (userDto.getPreference() != PreferredContact.TELEGRAM) {
            return;
        }

        if (userDto.getTelegramUserName() == null || userDto.getTelegramUserName().isBlank()) {
            throw new DataValidationException(String.format(
                    "With preferred telegram contact type must be provided telegram user name. User %d",
                    userDto.getId()));
        }
    }
}
