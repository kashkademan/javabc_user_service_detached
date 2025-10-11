package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.entity.user.Education;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EducationMapper {

    default Education toEducation(EducationCreateDto educationCreateDto) {
        if (educationCreateDto == null) {
            return null;
        }
        return Education
                .builder()
                .yearFrom(educationCreateDto.yearFrom())
                .yearTo(educationCreateDto.yearTo())
                .institution(educationCreateDto.institution())
                .educationLevel(educationCreateDto.educationLevel())
                .specialization(educationCreateDto.specialization())
                .build();
    };

    EducationDto toEducationDto(Education education);

    default void updateEducationFromDto(EducationUpdateDto educationUpdateDto, Education education) {
        if (educationUpdateDto == null || education == null) {
            return;
        }

        if (educationUpdateDto.yearFrom() != null) {
            education.setYearFrom(educationUpdateDto.yearFrom());
        }
        if (educationUpdateDto.yearTo() != null) {
            education.setYearTo(educationUpdateDto.yearTo());
        }
        if (educationUpdateDto.institution() != null) {
            education.setInstitution(educationUpdateDto.institution());
        }
        if (educationUpdateDto.educationLevel() != null) {
            education.setEducationLevel(educationUpdateDto.educationLevel());
        }
        if (educationUpdateDto.specialization() != null) {
            education.setSpecialization(educationUpdateDto.specialization());
        }
    };
}
