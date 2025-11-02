package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersByIds(
            @RequestBody
            @NotEmpty(message = "IDs list must not be empty")
            List<@NotNull(message = "ID must not be null")
            @Positive(message = "ID must be positive") Long> ids) {

        return ids.stream()
                .map(userService::getById)
                .toList();
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid CreateUserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable @Positive long userId,
                          @RequestBody @Valid UpdateUserDto userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Positive long userId) {
        return userService.getById(userId);
    }
}