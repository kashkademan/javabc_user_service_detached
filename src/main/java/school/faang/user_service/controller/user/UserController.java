package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.service.UserPictureService;
import school.faang.user_service.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public List<UserDto> getUsersByIds(@RequestBody List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @GetMapping("/{userId}/personal")
    public UserPersonalDto getUserPersonal(@PathVariable Long userId) {
        return userService.getUserPersonals(userId);
    }

    @PatchMapping("/{userId}/refresh")
    public UserPersonalDto refreshUsersAvatar(@PathVariable Long userId){
        return userService.refreshUsersAvatar(userId);
    }
}
