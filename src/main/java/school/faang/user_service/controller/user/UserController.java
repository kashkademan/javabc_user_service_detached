package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    public UserDto create(@Valid CreateUserDto userDto) {

        return userService.create(userDto);
    }

    public UserDto update(long userId, @Valid UpdateUserDto userDto) {

        return userService.update(userId, userDto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
    }
}
