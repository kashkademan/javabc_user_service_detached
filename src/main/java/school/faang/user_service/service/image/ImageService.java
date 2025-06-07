package school.faang.user_service.service.image;

import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.service.s3.S3Folder;
import school.faang.user_service.service.s3.S3Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;

    @Retryable(retryFor = {FeignException.class, RetryableException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    public Resource generateRandomUserAvatar(long userId) {
        byte[] image = diceBearClient.getRandomAvatar();
        log.info("Generated random avatar for user with ID {}", userId);

        MediaType type = new MediaType("image", "svg+xml");
        String fileName = String.format("user_%d_default_avatar.svg", userId);

        String fileKey = s3Service.uploadFile(image, fileName, type, S3Folder.AVATARS);

        Resource resource = new Resource();
        resource.setFileKey(fileKey);
        resource.setFileName(fileName);
        resource.setContentType(type.toString());
        resource.setSize((long) image.length);

        return resource;
    }
}
