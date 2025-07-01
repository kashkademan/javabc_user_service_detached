package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearRestTemplate;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.util.file.CustomMultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarGeneratorService {
    private final S3Service s3Service;
    private final DiceBearRestTemplate diceBearRestTemplate;

    @Value("${services.dicebear.version}")
    private String version;

    @Value("${services.dicebear.style}")
    private String style;

    @Value("${services.dicebear.format}")
    private String format;

    @Value("${services.s3.avatarPath}")
    private String avatarFolder;

    public String generateAndUpload() {
        MultipartFile avatarFile = generateAvatar();

        return s3Service.uploadFile(avatarFolder, avatarFile);
    }

    private MultipartFile generateAvatar() {
        String seed = UUID.randomUUID().toString();
        byte[] avatarBytes = diceBearRestTemplate.getAvatar(
                version,
                style,
                format,
                seed
        );

        return CustomMultipartFile.builder()
                .content(avatarBytes)
                .name("avatar")
                .originalFilename("generated_avatar_" + seed + ".png")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build();
    }
}