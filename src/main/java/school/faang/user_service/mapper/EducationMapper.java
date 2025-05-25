package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

   EducationDto toDto(Education education);

    Education toEntity(EducationDto educationDto);

    EducationDto toEducationDto(Education education);

    Education toEducation(EducationDto educationDto);
}