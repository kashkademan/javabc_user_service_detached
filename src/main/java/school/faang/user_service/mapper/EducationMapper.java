package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
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
     * Преобразует DTO создания образования в сущность {@link Education}.
     */
    Education toEducation(CreateEducationDto createEducationDto);

    /**
     * Обновляет DTO образования в сущность {@link Education}.
     */
    void educationUpdateFromDto(UpdateEducationDto updateEducationDto, @MappingTarget Education education);

    /**
     * Преобразует сущность {@link Education} в DTO для отображения.
     */
    EducationViewDto toEducationDto(Education education);
}
