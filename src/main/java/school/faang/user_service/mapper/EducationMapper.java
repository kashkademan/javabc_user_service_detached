package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EducationMapper {

    @Mapping(target = "user", ignore = true)
    Education toEducation(CreateEducationDto createEducationDto);

    EducationDto toEducationDto(Education education);

    default void updateEducationFromDto(UpdateEducationDto updateEducationDto, @MappingTarget Education education) {

        education.setEducationLevel(updateEducationDto.educationLevel());
        education.setYearFrom(updateEducationDto.yearFrom());
        education.setInstitution(updateEducationDto.institution());
        education.setSpecialization(education.getSpecialization());
        education.setYearTo(updateEducationDto.yearTo());
    };
}
