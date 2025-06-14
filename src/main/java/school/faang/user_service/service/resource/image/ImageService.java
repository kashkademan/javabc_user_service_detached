package school.faang.user_service.service.resource.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.dice_bear.DiceBearClient;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.service.resource.ResourceService;
import school.faang.user_service.service.s3.S3Folder;
import school.faang.user_service.service.s3.S3KeyGenerator;
import school.faang.user_service.service.s3.S3Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final DiceBearClient diceBearClient;
    private final S3Service s3Service;
    private final ResourceService resourceService;
    private final S3KeyGenerator s3KeyGenerator;

    public Resource generateRandomUserAvatar(long userId) {
        MediaType type = new MediaType("image", "svg+xml");
        String fileName = String.format("user_%d_default_avatar.svg", userId);
        String fileKey = s3KeyGenerator.generateKey(fileName, S3Folder.AVATARS, userId);


        byte[] image = diceBearClient.getRandomAvatar(type);
        log.info("Generated random avatar for user with ID {}", userId);

        s3Service.uploadFile(image, fileKey, type);

        Resource resource = new Resource();
        resource.setFileKey(fileKey);
        resource.setFileName(fileName);
        resource.setContentType(type.toString());
        resource.setSize((long) image.length);

        return resourceService.createResource(resource);
    }
}
