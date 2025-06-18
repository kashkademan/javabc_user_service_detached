package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.exception.avatar.AvatarGenerationException;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.util.file.CustomMultipartFile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarGeneratorService {
    private final S3Service s3Service;
    private final DiceBearClient dicebearClient;
    private final UserContext userContext;

    @Value("${services.dicebear.version}")
    private String version;

    @Value("${services.dicebear.style}")
    private String style;

    @Value("${services.dicebear.format}")
    private String format;

    @Value("${services.s3.avatarPath}")
    private String avatarFolder;

    @Async("avatar-generator-executor")
    public CompletableFuture<String> generateAndUpload() {
        userContext.setUserId(0); // Явно прописал 0, т.к не знаю, как убрать необходимость в хедере, ничего не помогло

        try {
            MultipartFile avatarFile = generateAvatar();
            log.info("Аватар успешно сгенерирован");

            return CompletableFuture.completedFuture(s3Service.uploadFile(avatarFolder, avatarFile));
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

        return CustomMultipartFile.builder()
                .content(avatarBytes)
                .name("avatar")
                .originalFilename("generated_avatar.png")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .build();
    }
}