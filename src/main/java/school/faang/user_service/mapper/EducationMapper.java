package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;

/**
 * EducationMapper — для преобразования между сущностью {@link Education} и DTO.
 * <p>
 * Представляет методы для конвертации данных.
 * </p>*
 *
 * @author fomchenkoandrey
 */
@Mapper(componentModel = "spring")
public interface EducationMapper {

    /**
     * Преобразует DTO создания расписания в сущность {@link Education}.
     */
    Education toEducation(EducationViewDto educationDto);

    /**
     * Преобразует сущность {@link Education} в DTO для отображения.
     */
    EducationViewDto toEducationDto(Education education);
}
