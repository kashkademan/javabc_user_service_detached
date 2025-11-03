package school.faang.user_service.controller.avatar;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.s3.S3Service;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/avatar")
@RestController
public class AvatarController {

    private final S3Service s3Service;
    private final AvatarService avatarService;

    @GetMapping("/{userId}")
    public ResponseEntity<Resource> getAvatarUsers(@PathVariable Long userId) {

        String smallFileId = avatarService.getAvatarUsers(userId);
        var metadata = s3Service.getFileMetadata(smallFileId);
        byte[] fileBytes = s3Service.downloadAvatarAsBytes(smallFileId);

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.contentType()))
                .contentLength(fileBytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + smallFileId + "\"")
                .body(resource);
    }
}
