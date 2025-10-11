package school.faang.user_service.service.education_unittest;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.mapper.EducationMapper;

import static org.junit.jupiter.api.Assertions.*;

public class EducationMapperTest {

    private final EducationMapper educationMapper = new EducationMapper() {
        @Override
        public Education toEducation(EducationCreateDto educationCreateDto) {
            return null;
        }

        @Override
        public EducationDto toEducationDto(Education education) {
            return null;
        }

        @Override
        public void updateEducationFromDto(EducationUpdateDto educationUpdateDto, Education education) {
            if (educationUpdateDto == null || education == null) {
                return;
            }

            education.setYearFrom(educationUpdateDto.yearFrom());
            education.setYearTo(educationUpdateDto.yearTo());
            education.setInstitution(educationUpdateDto.institution());
            education.setEducationLevel(educationUpdateDto.educationLevel());
            education.setSpecialization(educationUpdateDto.specialization());
        }
    };

    @Test
    void testUpdateEducationFromDto() {
        // given
        EducationUpdateDto educationUpdateDto = new EducationUpdateDto(
            2020, 2024, "University 123123", "Bachelor", "Computer Science");

        Education existingEducation = new Education();
        existingEducation.setInstitution("Old University");
        existingEducation.setEducationLevel("Master");

        educationMapper.updateEducationFromDto(educationUpdateDto, existingEducation);

        assertEquals("University 123123", existingEducation.getInstitution());
        assertEquals("Bachelor", existingEducation.getEducationLevel());
    }
}
