package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper
public interface EducationMapper {
    public Education toEducation(EducationDto educationDto);

    public EducationDto toEducationDto(Education education);
}
