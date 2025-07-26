package school.faang.user_service.avatar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.avatar.dto.AvatarDto;
import school.faang.user_service.avatar.provider.AvatarFile;
import school.faang.user_service.avatar.provider.AvatarProvider;
import school.faang.user_service.avatar.storage.FileStorage;

import java.util.UUID;

/**
 * Сервис для работы с аватарами пользователей.
 * <p>
 * Этот класс предоставляет методы для генерации и загрузки аватаров пользователей.
 * Он использует {@link AvatarProvider} для генерации аватаров и {@link FileStorage} для их загрузки в хранилище.
 * </p>
 *
 * @author agent
 * @since 26.07.2025
 */
@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private final AvatarProvider avatarProvider;
    private final FileStorage fileStorage;

    /**
     * Генерирует и загружает аватар для пользователя.
     *
     * @param username имя пользователя, для которого нужно сгенерировать и загрузить аватар
     * @return объект {@link AvatarDto}, содержащий URL загруженного аватара
     */
    public AvatarDto generateAndUpload(String username) {
        String key = UUID.randomUUID() + "-" + username;
        AvatarFile avatar = avatarProvider.generate(key);

        String path = "avatars/" + key + ".svg";
        String url = fileStorage.upload(path, avatar.getContent(), avatar.getContentLength(), avatar.getContentType());

        return new AvatarDto(url);
    }
}