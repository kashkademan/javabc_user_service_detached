package school.faang.user_service.controller.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.useravatar.ApiResponse;
import school.faang.user_service.service.avatar.UserAvatarService;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatars")
@RestController
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @PostMapping
    public ResponseEntity<ApiResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            userAvatarService.uploadAvatar(file);
            return ResponseEntity.ok(ApiResponse.builder()
                    .message("Avatar uploaded successfully")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message("Failed to upload avatar: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }

    @GetMapping
    public ResponseEntity<byte[]> getAvatar(@RequestParam(defaultValue = "false") boolean small) {

        byte[] avatarData = userAvatarService.getAvatar(small);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(avatarData.length);
        headers.setCacheControl("max-age=3600"); // Кэшируем на 1 час

        return new ResponseEntity<>(avatarData, headers, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAvatar() {
        try {
            userAvatarService.deleteAvatar();
            return ResponseEntity.ok(ApiResponse.builder()
                    .message("Avatar deleted successfully")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .message("Failed to delete avatar: " + e.getMessage())
                    .success(false)
                    .build());
        }
    }
}
