package school.faang.user_service.controller.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserAvatarUploadDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserDto create(CreateUserDto userDto) {
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

    @PostMapping("/avatars")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadAvatar(@ModelAttribute UserAvatarUploadDto userAvatarUploadDto)
    {
        userService.uploadAvatar(userAvatarUploadDto);
    }

    @DeleteMapping("/avatars")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar() {
        userService.deleteAvatar();
    }

    @GetMapping("/{userId}/avatars")
    public ResponseEntity<byte[]> getAvatar(
            @PathVariable
            @Positive
            Long userId,
            @RequestParam(defaultValue = "big")
            String size
    ) {
        return userService.getAvatar(userId, size);
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }
}
