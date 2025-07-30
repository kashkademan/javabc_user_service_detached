package school.faang.user_service.service.education;

import school.faang.user_service.dto.education.EducationDto;

public interface EducationService {

    EducationDto addEducation(long userId, EducationDto educationDto);

    void updateEducation(long educationId, EducationDto educationDto);

    EducationDto getEducationById(long educationId);
}
