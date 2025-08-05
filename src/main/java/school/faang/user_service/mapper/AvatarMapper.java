package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.avatar.AvatarDownloadDto;

/**
 * Маппер для преобразования данных аватара в DTO для скачивания.
 * <p>
 * Предоставляет методы для конвертации данных аватара (байтовый массив, MIME-тип, имя файла)
 * в DTO объект {@link AvatarDownloadDto}.
 * </p>
 *
 * @author Linempy
 * @since 04.08.2025
 */
@Mapper(componentModel = "spring")
public interface AvatarMapper {

    default AvatarDownloadDto toDownloadDto(byte[] content, String contentType, String fileName) {
        return new AvatarDownloadDto(content, contentType, fileName, (long) content.length);
    }
}