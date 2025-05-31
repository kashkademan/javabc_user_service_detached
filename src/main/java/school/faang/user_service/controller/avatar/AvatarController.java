
package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.avatar.AvatarDto;
import school.faang.user_service.service.avatar.AvatarService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/{userId}/avatar")
    public AvatarDto getUserAvatar(@PathVariable @Positive(message = "User ID must be positive") Long userId) {
        return avatarService.getAvatarDto(userId);
    }

    @PostMapping("/{userId}/avatar/generate")
    public AvatarDto generateUserAvatar(@PathVariable @Positive(message = "User ID must be positive") Long userId) {
        return avatarService.generateAvatarDto(userId);
    }

    @GetMapping("/{userId}/avatar/url")
    public String getAvatarUrl(@PathVariable @Positive(message = "User ID must be positive") Long userId) {
        return avatarService.getAvatarUrl(userId);
    }
}