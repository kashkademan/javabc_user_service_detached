package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;

@Mapper
public interface EducationMapper {
    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);
}
