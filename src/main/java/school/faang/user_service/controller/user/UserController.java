package school.faang.user_service.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserFullDto;
import school.faang.user_service.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/newuser?{filter}")
    public Long newUser(@Valid @RequestBody UserFullDto dto, @PathVariable String filter) throws IOException {
        return userService.newUser(dto, filter);
    }

    @PostMapping("/newuser")
    public Long newUser(@Valid @RequestBody UserFullDto dto) throws IOException {
        return userService.newUser(dto, null);
    }
}
