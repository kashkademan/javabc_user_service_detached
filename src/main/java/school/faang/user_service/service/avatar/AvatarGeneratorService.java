package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.exception.avatar.AvatarGenerationException;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.util.file.CustomMultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarGeneratorService {
    private final S3Service s3Service;
    private final DiceBearClient dicebearClient;

    @Value("${services.dicebear.version}")
    private String version;

    @Value("${services.dicebear.style}")
    private String style;

    @Value("${services.dicebear.format}")
    private String format;

    @Value("${services.s3.avatarPath}")
    private String avatarFolder;

    public String generateAndUpload() {
        try {
            MultipartFile avatarFile = generateAvatar();
            log.info("Аватар успешно сгенерирован");

            return s3Service.uploadFile(avatarFolder, avatarFile);
        } catch (AvatarGenerationException e) {
            log.error("Ошибка при генерации аватара: {}", e.getMessage(), e);
            throw new AvatarGenerationException("Ошибка при генерации аватара");
        }
    }

    private MultipartFile generateAvatar() {
        String seed = UUID.randomUUID().toString();
        byte[] avatarBytes = dicebearClient.getAvatar(
                version,
                style,
                format,
                seed
        );

        return new CustomMultipartFile(
                avatarBytes,
                "avatar",
                "generated_avatar.png",
                MediaType.IMAGE_PNG_VALUE
        );
    }
}