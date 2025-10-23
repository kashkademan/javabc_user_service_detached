package school.faang.user_service.service.education;

import school.faang.user_service.dto.user.EducationDto;

public interface EducationService {

    EducationDto addEducation(Long userId, EducationDto educationDto);

    EducationDto updateEducation(Long userId, Long educationId, EducationDto educationDto);

    EducationDto getById(Long educationId);

}
