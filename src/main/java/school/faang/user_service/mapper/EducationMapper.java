package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

    @Mapping(source =  "educationDto.id", target = "id")
    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education savedEducation);

    EducationDto toDto(Education save);

    @Mapping(source =  "educationDto.id", target = "id")
    Education toEntity(EducationDto educationDto, User user);
}