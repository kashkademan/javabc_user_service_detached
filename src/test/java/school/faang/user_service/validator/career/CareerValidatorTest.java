package school.faang.user_service.validator.career;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CareerValidatorTest {

    @Test
    public void testValidateCareerDates_ValidDates() {
        CreateCareerDto createCareerDto = new CreateCareerDto(
                LocalDate.now().minusYears(2),
                LocalDate.now().minusMonths(1),
                "Google",
                "Software Engineer"
        );
        CareerValidator.validateCareerDates(createCareerDto);
    }

    @Test
    public void testValidateCareerDates_validFromAndNullTo_shouldPass() {
        CreateCareerDto createCareerDto = new CreateCareerDto(
                LocalDate.now().minusYears(1),
                null,
                "Google",
                "Software Engineer"
        );
        CareerValidator.validateCareerDates(createCareerDto);
    }

    @Test
    public void testValidateCareerDates_fromIsToday_shouldFail() {
        CreateCareerDto createCareerDto = new CreateCareerDto(
                LocalDate.now(),
                null,
                "Google",
                "Software Engineer"
        );
        Assertions.assertThrows(DataValidationException.class, () -> {
            CareerValidator.validateCareerDates(createCareerDto);
        });
    }

    @Test
    public void testValidateCareerDates_fromAfterTo_shouldFail() {
        CreateCareerDto createCareerDto = new CreateCareerDto(
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(20),
                "Google",
                "Software Engineer"
        );
        Assertions.assertThrows(DataValidationException.class, () -> {
            CareerValidator.validateCareerDates(createCareerDto);
        });
    }

    @Test
    public void testValidateOwner_validOwner_shouldPass() {
        User user = new User();
        user.setId(1L);
        Career career = new Career();
        career.setUser(user);
        CareerValidator.validateOwner(career, 1L);
    }

    @Test
    public void testValidateOwner_invalidOwner_shouldFail() {
        User user = new User();
        user.setId(1L);
        Career career = new Career();
        career.setUser(user);
        CareerValidator.validateOwner(career, 1L);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 2L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }

    @Test
    public void testValidateOwner_nullUser_shouldFail() {
        Career career = new Career();
        career.setUser(null);
        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 1L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }

    @Test
    void validateOwner_userWithNullId_shouldFail() {
        User user = new User();
        user.setId(null);
        Career career = new Career();
        career.setUser(user);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 1L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }
}