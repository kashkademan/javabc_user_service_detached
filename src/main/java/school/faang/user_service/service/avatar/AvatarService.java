package school.faang.user_service.service.avatar;

import school.faang.user_service.dto.avatar.AvatarDownloadDto;
import school.faang.user_service.exception.AvatarProcessingException;
import school.faang.user_service.exception.EntityNotFoundException;

/**
 * Интерфейс сервиса для работы с аватарами пользователей.
 * <p>
 * Предоставляет методы для:
 * <ul>
 *   <li>Генерации и сохранения нового аватара</li>
 *   <li>Загрузки существующего аватара</li>
 * </ul>
 * </p>
 *
 * @see AvatarDownloadDto
 * @see AvatarProcessingException
 *
 * @author Linempy
 * @since 06.08.2025
 */
public interface AvatarService {

    /**
     * Генерирует и сохраняет новый аватар для текущего пользователя.
     *
     * @throws AvatarProcessingException если возникла ошибка при генерации или сохранении
     * @throws EntityNotFoundException если аватар не найден
     */
    void generateAndSaveAvatar();

    /**
     * Загружает аватар текущего пользователя.
     *
     * @return DTO с данными аватара
     * @throws AvatarProcessingException если возникла ошибка при загрузке
     * @throws EntityNotFoundException если аватар не найден
     */
    AvatarDownloadDto downloadAvatar();
}