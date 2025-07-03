package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.dto.UserTelegramDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Long userId);
    UserDto updateUser(UserDto userDto);
    List<UserDto> getUsersByIds(List<Long> userIds);
    UserPersonalDto getUserPersonals(Long userId);
    UserPersonalDto refreshUserAvatar(Long userId);

    List<UserDto> processCsv(MultipartFile file);
    void banUser(Long userId);
    void unbanUser(Long userId);
    UserTelegramDto addUserTelegram(UserTelegramDto userTelegramDto);
    UserTelegramDto getUserTelegram(long userId);
    UserTelegramDto getUserByTelegram(String telegramUserName);
}