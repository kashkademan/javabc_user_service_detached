package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;

/**
 * EducationMapper — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>*
 *
 * @author Пользователь
 * @since 04.07.2025
 */
@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationViewDto educationDto);

    EducationViewDto toEducationDto(Education education);
}
