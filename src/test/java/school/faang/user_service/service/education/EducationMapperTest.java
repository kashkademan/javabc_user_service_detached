package school.faang.user_service.service.education;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.education.EducationCreateDto;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.dto.education.EducationUpdateDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.mapper.EducationMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EducationMapperTest {

    private EducationMapper educationMapper;
    private Education education;
    private EducationCreateDto createDto;
    private EducationUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        educationMapper = Mappers.getMapper(EducationMapper.class);

        education = Education.builder()
                .id(1L)
                .yearFrom(2000)
                .yearTo(2005)
                .institution("University")
                .educationLevel("BACHELOR")
                .specialization("Computer Science")
                .build();

        createDto = new EducationCreateDto(
                2000,
                2005,
                "University",
                "BACHELOR",
                "Computer Science"
        );

        updateDto = new EducationUpdateDto(
                2001,
                2006,
                "University123",
                "MASTER",
                "SoftwareEngineering"
        );
    }

    // Тест маппинга из EducationCreateDto в Education
    @Test
    void toEducationWithValidCreateDtoReturnsEducation() {
        Education result = educationMapper.toEducation(createDto);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(createDto.yearFrom(), result.getYearFrom(), "Год начала должен совпадать");
        assertEquals(createDto.yearTo(), result.getYearTo(), "Год окончания должен совпадать");
        assertEquals(createDto.institution(), result.getInstitution(), "Название учреждения должно совпадать");
        assertEquals(createDto.educationLevel(), result.getEducationLevel(), "Уровень образования должен совпадать");
        assertEquals(createDto.specialization(), result.getSpecialization(), "Специализация должна совпадать");
    }

    // Тест маппинга из Education в EducationDto
    @Test
    void toEducationDtoWithValidEducationReturnsEducationDto() {
        EducationDto result = educationMapper.toEducationDto(education);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(education.getId(), result.id(), "ID должен совпадать");
        assertEquals(education.getYearFrom(), result.yearFrom(), "Год начала должен совпадать");
        assertEquals(education.getYearTo(), result.yearTo(), "Год окончания должен совпадать");
        assertEquals(education.getInstitution(), result.institution(), "Название учреждения должно совпадать");
        assertEquals(education.getEducationLevel(), result.educationLevel(), "Уровень образования должен совпадать");
        assertEquals(education.getSpecialization(), result.specialization(), "Специализация должна совпадать");
    }

    // Тест обновления Education из EducationUpdateDto
    @Test
    void updateEducationFromDtoWithValidUpdateDtoUpdatesEducation() {
        educationMapper.updateEducationFromDto(updateDto, education);

        assertEquals(updateDto.yearFrom(), education.getYearFrom(), "Год начала должен обновиться");
        assertEquals(updateDto.yearTo(), education.getYearTo(), "Год окончания должен обновиться");
        assertEquals(updateDto.institution(), education.getInstitution(), "Название учреждения должно обновиться");
        assertEquals(
                updateDto.educationLevel(),
                education.getEducationLevel(),
                "Уровень образования должен обновиться"
        );
        assertEquals(updateDto.specialization(), education.getSpecialization(), "Специализация должна обновиться");
    }

    // Тест частичного обновления Education
    @Test
    void updateEducationFromDtoWithPartialUpdateUpdatesOnlyProvidedFields() {
        EducationUpdateDto partialUpdate = new EducationUpdateDto(
                null,
                2006,
                null,
                "MASTER",
                null
        );

        educationMapper.updateEducationFromDto(partialUpdate, education);

        // Проверяем, что обновились только указанные поля
        assertEquals(2000, education.getYearFrom(), "Год начала не должен измениться");
        assertEquals(2006, education.getYearTo(), "Год окончания должен обновиться");
        assertEquals("University", education.getInstitution(), "Название учреждения не должно измениться");
        assertEquals("MASTER", education.getEducationLevel(), "Уровень образования должен обновиться");
        assertEquals("Computer Science", education.getSpecialization(), "Специализация не должна измениться");
    }
}

