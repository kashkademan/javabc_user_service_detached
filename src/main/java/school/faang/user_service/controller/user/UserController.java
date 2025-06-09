package school.faang.user_service.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserPersonalDto;
import school.faang.user_service.service.UserService;

import java.util.List;

@Slf4j
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
        UserPersonalDto userPersonals = userService.getUserPersonals(userId);
        log.debug("Personal info was provided for userid {}", userId);

        return userPersonals;
    }

    @PatchMapping("/{userId}/refresh")
    public UserPersonalDto refreshUsersAvatar(@PathVariable Long userId) {
        UserPersonalDto personalDto = userService.refreshUserAvatar(userId);
        log.debug("Personal photo was refreshed for userid {}, new avatar is {}",
                userId, personalDto.getPictureSmallFileId());

        return personalDto;
    }

    @PostMapping("/upload-csv")
    public List<UserDto> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new DataValidationException("File not found");
        }

        return userService.processCsv(file);
    }
}