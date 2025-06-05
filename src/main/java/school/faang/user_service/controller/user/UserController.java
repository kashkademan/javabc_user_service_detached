package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.user.UserServiceFacade;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServiceFacade userService;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersById(ids);
    }
}