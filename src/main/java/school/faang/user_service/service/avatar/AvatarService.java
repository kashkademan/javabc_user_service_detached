package school.faang.user_service.service.avatar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.avatar.AvatarDownloadDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.StorageException;
import school.faang.user_service.mapper.AvatarMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.file_storage.FileStorageService;

/**
 * Сервис для работы с аватарами пользователей.
 * Обеспечивает генерацию, сохранение и загрузку аватаров.
 *
 * @author Linempy
 * @since 03.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {
    private static final String CONTENT_TYPE = "png";

    private final UserContext context;
    private final DiceBearAvatarService service;
    private final FileStorageService fileStorage;
    private final UserRepository repository;
    private final AvatarMapper mapper;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Transactional
    public void generateAndSaveAvatar() {
        Long userId = context.getUserId();

        byte[] avatarBytes = service.generateRandomAvatar();
        String objectKey = generateAvatarKey(userId);

        uploadAvatarToStorage(avatarBytes, userId, objectKey);

        saveAvatarKeyToUser(userId, objectKey);
    }

    public AvatarDownloadDto downloadAvatar() {
        Long userId = context.getUserId();
        String objectKey = repository.getAvatarKeyByIdOrThrow(userId);

        byte[] avatarBytes = downloadAvatarFromStorage(userId, objectKey);
        log.info("Успешное скачивание аватарки. Пользователь id={}", userId);

        return mapper.toDownloadDto(
                avatarBytes, MediaType.IMAGE_PNG_VALUE, "avatar_user_" + userId + ".png"
        );
    }

    private byte[] downloadAvatarFromStorage(Long userId, String objectKey) {
        try {
            return fileStorage.download(bucketName, objectKey);
        } catch (Exception e) {
            log.error("Ошибка при скачивании аватарки. Пользователь ID: {}, Ключ: {}", userId, objectKey);
            throw new AvatarProcessingException("Ошибка загрузки аватарки", e);
        }
    }

    private void uploadAvatarToStorage(byte[] avatarBytes, Long userId, String objectKey) {
        try {
            fileStorage.upload(avatarBytes, bucketName, objectKey, MediaType.IMAGE_PNG_VALUE);
            log.info("Аватарка сгенерирована для пользователя id= {}", userId);
        } catch (StorageException e) {
            log.error("Ошибка генерации аватарки для пользователя id= {}", userId, e);
            throw new AvatarProcessingException("В процессе генерации аватарки произошла ошибка", e);
        }
    }

    private void saveAvatarKeyToUser(Long userId, String objectKey) {
        User user = repository.getByIdOrThrow(userId);
        user.setAvatarKey(objectKey);
        repository.save(user);
    }

    private String generateAvatarKey(Long userId) {
        return String.format(
                "ava/user_%d_%d.%s",
                userId, System.currentTimeMillis(), CONTENT_TYPE
        );
    }
}