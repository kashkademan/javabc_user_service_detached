package school.faang.user_service.controller.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatar")
@RestController
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getAvatarUsers(@PathVariable Long userId) {
        return avatarService.getAvatarUsers(userId);
    }
}
