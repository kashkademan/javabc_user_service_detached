package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserTelegramDto;

public interface TelegramService {
    void validateTelegramUserId(UserTelegramDto userTelegramDto, long userId);
    void validateTelegramChatId(long providedChatId, Long chatId, long userId);
    void validateTelegramPreference(UserDto userDto);
}
