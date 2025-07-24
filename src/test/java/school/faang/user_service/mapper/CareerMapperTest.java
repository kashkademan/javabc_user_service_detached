package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.career.CareerCreateDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CareerMapperTest {

    private final CareerMapper mapper = Mappers.getMapper(CareerMapper.class);

    @Test
    void toEntity_shouldMapFieldsCorrectly() {
        CareerCreateDto dto = new CareerCreateDto(
                LocalDate.of(2025, 8, 19),
                LocalDate.of(2025, 10, 19),
                "Company",
                "Engineer"
        );
        User user = new User();
        user.setId(1L);

        Career result = mapper.toEntity(dto, user);

        assertNotNull(result);
        assertEquals("Company", result.getCompany());
        assertEquals("Engineer", result.getPosition());
        assertEquals(user, result.getUser());
    }

    @Test
    void update_shouldApplyChanges() {
        Career career = new Career();
        career.setCompany("NewCompany");
        career.setPosition("Intern");
        career.setDateFrom(LocalDate.of(2018, 8, 19));
        career.setDateTo(LocalDate.of(2020, 10, 19));

        UpdateCareerDto dto = new UpdateCareerDto(
                LocalDate.of(2020, 8, 19),
                LocalDate.of(2024, 10, 19),
                "NewCompany",
                "Lead"
        );

        mapper.update(dto, career);

        assertEquals("NewCompany", career.getCompany());
        assertEquals("Lead", career.getPosition());
        assertEquals(LocalDate.of(2018, 8, 19), career.getDateFrom());
        assertEquals(LocalDate.of(2020, 10, 19), career.getDateTo());
    }
}
