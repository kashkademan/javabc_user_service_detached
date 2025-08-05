package school.faang.user_service.dto.avatar;


/**
 * DTO для скачивания аватара.
 * Содержит бинарные данные файла, метаинформацию и размер.
 *
 * @param content бинарное содержимое файла
 * @param contentType MIME-тип содержимого (например, "image/png")
 * @param fileName имя файла для сохранения
 * @param size размер файла в байтах (положительное число)
 *
 * @author Linempy
 * @since 04.08.2025
 */
public record AvatarDownloadDto(
        byte[] content,
        String contentType,
        String fileName,
        Long size
) {
}