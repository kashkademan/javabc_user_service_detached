package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public UserDto create(@RequestBody CreateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateString(userDto.password(), "password");
        validateNotNull(userDto.countryId(), "country");
        return userService.create(userDto);
    }

    public UserDto update(long userId, UpdateUserDto userDto) {
        validateString(userDto.username(), "username");
        validateString(userDto.email(), "email");
        validateNotNull(userDto.countryId(), "country");
        return userService.update(userId, userDto);
    }

    public UserDto getById(long userId) {
        return userService.getById(userId);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    @GetMapping("/promotion")
    public Page<UserDto> getUser(@PageableDefault Pageable pageable) {
        Page<UserDto> results = userServiceImpl.getUser(pageable);
        return results;
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
