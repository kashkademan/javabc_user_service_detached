package school.faang.user_service.service.education;

import school.faang.user_service.dto.user.EducationViewDto;

/**
 * EducationService — описание интерфейса.
 * <p>
 *TODO: описать, какие обязанности реализует интерфейс.
 * </p>*
 *
 * @author Пользоват ель
 * @since 04.07.2025
 */

public interface EducationService {

    EducationViewDto addEducation(long userId, EducationViewDto educationDto);

    EducationViewDto updateEducation(long userId, long educationId, EducationViewDto educationDto);
}
