package school.faang.user_service.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
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
import school.faang.user_service.dto.response.UploadAvatarResponseDto;
import school.faang.user_service.service.user.UserAvatarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatar")
public class UserAvatarController {

    private final UserAvatarService userAvatarService;
    private static final String USER_ID_PATH = "/{userId}";
    private static final String USER_SMALL_PIC_PATH = "/small";

    @PostMapping(USER_ID_PATH)
    @Operation(
            summary = "Upload user avatar",
            description = "Uploads an avatar image (max 5MB) for the user with the specified ID"
    )
    public ResponseEntity<UploadAvatarResponseDto> uploadAvatar(
            @PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        UploadAvatarResponseDto uploadAvatarResponseDto = userAvatarService.uploadAvatar(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadAvatarResponseDto);
    }

    @GetMapping(USER_ID_PATH)
    @Operation(
            summary = "Download user's large avatar",
            description = "Returns the large version (1080px) of the user's avatar image as JPEG. " +
                    "Responds with 404 if the user or avatar is not found."
    )
    public ResponseEntity<InputStreamResource> downloadLargeAvatar(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(userAvatarService.downloadLargeAvatar(userId));
    }

    @GetMapping(USER_ID_PATH + USER_SMALL_PIC_PATH)
    @Operation(
            summary = "Download user's small avatar",
            description = "Returns the small version (170px) of the user's avatar image as JPEG. " +
                    "Responds with 404 if the user or avatar is not found."
    )
    public ResponseEntity<InputStreamResource> downloadSmallAvatar(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(userAvatarService.downloadSmallAvatar(userId));
    }

    @DeleteMapping(USER_ID_PATH)
    @Operation(
            summary = "Delete user's avatar",
            description = "Deletes both the large and small versions of the user's avatar image" +
                    " from S3 and clears avatar info from the database. " +
                    "Responds with 404 if the user is not found."
    )
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long userId) {
        userAvatarService.deleteAvatar(userId);
        return ResponseEntity.noContent().build();
    }
}
