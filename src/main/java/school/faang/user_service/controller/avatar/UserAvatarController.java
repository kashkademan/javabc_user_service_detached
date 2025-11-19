package school.faang.user_service.controller.avatar;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.service.avatar.UserAvatarService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatars")
@RestController
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @PostMapping
    public ResponseEntity<Void> uploadAvatar(@RequestParam("file") MultipartFile file) {
        userAvatarService.uploadAvatar(file);
        return ResponseEntity.ok().build();

    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> getAvatar(@RequestParam(defaultValue = "false") boolean small,
                                            @PathVariable @Positive Long userId) {

        byte[] avatarData = userAvatarService.getAvatar(small, userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(avatarData.length);
        headers.setCacheControl("max-age=3600"); // Кэшируем на 1 час

        return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAvatar() {
            userAvatarService.deleteAvatar();
            return ResponseEntity.ok().build();
    }
}
