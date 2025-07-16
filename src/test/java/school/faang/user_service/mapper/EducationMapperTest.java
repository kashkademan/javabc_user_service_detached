package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationViewDto;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * EducationMapperTest — описание класса.
 * <p>
 * Тестирует класс EducationMapper
 * </p>*
 *
 * @author Пользователь
 * @since 16.07.2025
 */

@DisplayName("Юнит-тесты для EducationMapper")
public class EducationMapperTest {

    private final EducationMapper mapper = Mappers.getMapper(EducationMapper.class);

    @Test
    @DisplayName("toEntity - проверка коректнного маппинга из CreateEducationDto в Education")
    public void testToEntity() {
        User user = new User();
        long userId = 1L;
        long educationId = 1L;
        user.setId(userId);
        CreateEducationDto dto = new CreateEducationDto();
        dto.setYearFrom(2014);
        dto.setYearTo(2018);
        dto.setInstitution("MTI");
        dto.setEducationLevel("Master’s Degree");
        dto.setSpecialization("engineer");

        Education education = new Education();
        education.setId(educationId);
        education.setYearFrom(2014);
        education.setYearTo(2018);
        education.setInstitution("MTI");
        education.setEducationLevel("Master’s Degree");
        education.setSpecialization("engineer");
        education.setUser(user);

        Education actual = mapper.toEntity(dto, user);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(education);
    }

    @Test
    @DisplayName("update - обновляет education на основе UpdateEducationDto")
    public void testUpdate() {
        User user = new User();
        UpdateEducationDto dto = new UpdateEducationDto(
               2014,
               2018,
               "MTI",
               "Master’s Degree",
                "engineer"
        );
        Education education = new Education();
        education.setId(1L);
        education.setYearFrom(2014);
        education.setYearTo(2018);
        education.setInstitution("MTI");
        education.setEducationLevel("Master’s Degree");
        education.setSpecialization("engineer");
        education.setUser(user);

        mapper.update(dto, education);

        Education expected = new Education();
        expected.setId(1L);
        expected.setYearFrom(2014);
        expected.setYearTo(2018);
        expected.setInstitution("MTI");
        expected.setEducationLevel("Master’s Degree");
        expected.setSpecialization("engineer");
        expected.setUser(user);

        assertThat(education)
                .usingRecursiveComparison()
                .ignoringFields("id", "user")
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("toViewDto - корректно маппит Education в EducationViewDto")
    public void testToViewDto() {
        User user = new User();
        user.setId(1L);
        Education education = new Education();
        education.setId(1L);
        education.setYearFrom(2014);
        education.setYearTo(2018);
        education.setInstitution("MTI");
        education.setEducationLevel("Master’s Degree");
        education.setSpecialization("engineer");
        education.setUser(user);

        EducationViewDto actualDto = mapper.toViewDto(education);
        EducationViewDto expectedDto = new EducationViewDto(
                1L,
                2014,
                2018,
                "MTI",
                "Master’s Degree",
                "engineer"
        );

        assertThat(actualDto)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedDto);
    }
}
