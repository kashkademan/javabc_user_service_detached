package school.faang.user_service.service;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto findUserById(Long userId);
    UserDto updateUser(UserDto userDto);
    List<UserDto> getUsersByIds(List<Long> userIds);
    List<UserDto> processCsv(MultipartFile multipartFile);
}