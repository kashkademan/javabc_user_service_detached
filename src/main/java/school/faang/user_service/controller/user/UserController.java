package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFullDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/newuser?{filter}")
    public Long newUser(@Valid @RequestBody UserFullDto dto, @PathVariable String filter) {
        return userService.createUser(dto, filter);
    }

    @PostMapping("/newuser")
    public Long newUser(@Valid @RequestBody UserFullDto dto) {
        return userService.createUser(dto, null);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable("userId") long id) {
        return userMapper.toDto(userService.getUserById(id));
    }
}
