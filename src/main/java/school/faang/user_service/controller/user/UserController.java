package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids) {
        if (ids.isEmpty()) {
            log.info("No userIds resolved from likes -> returning empty list");
            return List.of();
        }
        return userService.getUsersByIds(ids);
    }

    @PostMapping
    public UserDto create(@RequestBody @Valid CreateUserDto userDto) {
        return userService.create(userDto);
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable @Positive long userId, @RequestBody @Valid UpdateUserDto userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Positive long userId) {
        return userService.getById(userId);
    }
}