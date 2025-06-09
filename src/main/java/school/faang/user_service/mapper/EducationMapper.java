package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {
    Education toEducation(EducationDto educationDto);
    EducationDto toEducationDto(Education savedEducation);
    EducationDto toDto(Education save);
    Education toEntity(EducationDto educationDto);
}