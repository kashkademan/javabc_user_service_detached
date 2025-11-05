package school.faang.user_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;
import school.faang.user_service.dto.user.education.UpdateEducationDto;
import school.faang.user_service.entity.user.Education;


@Mapper(componentModel = "spring")
public interface EducationMapper {
    Education toEducation(CreateEducationDto createEducationDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEducationFromDto(UpdateEducationDto dto, @MappingTarget Education entity);

    EducationDto toEducationDto(Education education);
}
