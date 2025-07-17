package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;

/**
 * EducationMapper — для преобразования между сущностью {@link Education} и DTO.
 * <p>
 * Представляет методы для конвертации данных.
 * </p>*
 *
 * @author fomchenkoandrey
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationMapper {

    /**
     * Преобразует DTO создания образования в сущность {@link Education}.
     */
    @Mapping(target = "user", source = "user")
    Education toEntity(CreateEducationDto createEducationDto, User user);

    /**
     * Обновляет DTO образования в сущность {@link Education}.
     */
    void update(UpdateEducationDto updateEducationDto, @MappingTarget Education education);

    /**
     * Преобразует сущность {@link Education} в DTO для отображения.
     */
    EducationViewDto toViewDto(Education education);
}
