package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    @Mapping(source = "id", target = "user")
    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);
}
