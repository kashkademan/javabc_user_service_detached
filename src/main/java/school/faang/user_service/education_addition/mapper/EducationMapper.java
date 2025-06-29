package school.faang.user_service.education_addition.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.education_addition.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

    @Mapping(target = "user", ignore = true)
    Education toEntity(EducationDto dto);

    EducationDto toDto(Education education);
}