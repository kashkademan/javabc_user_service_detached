package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    void toDto(Education education);

    Education toEntity(EducationDto educationDto);

}
