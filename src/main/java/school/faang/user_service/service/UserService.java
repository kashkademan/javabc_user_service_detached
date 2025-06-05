package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface UserService {
    UserDto findUserById(Long userId);

    UserDto updateUser(UserDto userDto);

    List<UserDto> getUsersByIds(List<Long> userIds);

    List<UserDto> processCsv(InputStream inputStream) throws IOException;
}