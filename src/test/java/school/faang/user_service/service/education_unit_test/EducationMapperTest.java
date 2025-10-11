package school.faang.user_service.service.education_unit_test;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.mapper.EducationMapper;

import static org.junit.jupiter.api.Assertions.*;

public class EducationMapperTest {

    private final EducationMapper educationMapper = new EducationMapper() {
        @Override
        public Education toEducation(CreateEducationDto createEducationDto) {
            return null;
        }

        @Override
        public EducationDto toEducationDto(Education education) {
            return null;
        }

        @Override
        public void updateEducationFromDto(UpdateEducationDto updateEducationDto, Education education) {
            if (updateEducationDto == null || education == null) {
                return;
            }

            education.setYearFrom(updateEducationDto.yearFrom());
            education.setYearTo(updateEducationDto.yearTo());
            education.setInstitution(updateEducationDto.institution());
            education.setEducationLevel(updateEducationDto.educationLevel());
            education.setSpecialization(updateEducationDto.specialization());
        }
    };

    @Test
    void testUpdateEducationFromDto() {
        // given
        UpdateEducationDto updateEducationDto = new UpdateEducationDto(
            2020, 2024, "University 123123", "Bachelor", "Computer Science");

        Education existingEducation = new Education();
        existingEducation.setInstitution("Old University");
        existingEducation.setEducationLevel("Master");

        educationMapper.updateEducationFromDto(updateEducationDto, existingEducation);

        assertEquals("University 123123", existingEducation.getInstitution());
        assertEquals("Bachelor", existingEducation.getEducationLevel());
    }
}
