package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.EducationResponseDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    @Mapping(target = "user", ignore = true)
    Education toEducation(EducationResponseDto educationResponseDto);

    EducationResponseDto toEducationDto(Education education);
}
