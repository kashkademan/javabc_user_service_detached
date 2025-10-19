package school.faang.user_service.validator.career;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import school.faang.user_service.dto.career.BaseCareerDtoWithDates;
import school.faang.user_service.exception.DataValidationException;
import org.mockito.Mockito;
import school.faang.user_service.entity.user.Career;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

public class CareerValidatorTest {

    @Test
    public void testValidateCareerDates_ValidDates() {
        BaseCareerDtoWithDates baseDto = new BaseCareerDtoWithDates(
                LocalDate.now().minusYears(2),
                LocalDate.now().minusMonths(1)) {
        };
        CareerValidator.validateCareerDates(baseDto);
    }

    @Test
    public void testValidateCareerDates_validFromAndNullTo_shouldPass() {
        BaseCareerDtoWithDates baseDto = new BaseCareerDtoWithDates(LocalDate.now().minusYears(1), null) {
        };
        CareerValidator.validateCareerDates(baseDto);
    }

    @Test
    public void testValidateCareerDates_fromIsToday_shouldFail() {
        BaseCareerDtoWithDates baseDto = new BaseCareerDtoWithDates(LocalDate.now(), null) {
        };
        Assertions.assertThrows(DataValidationException.class, () -> {
            CareerValidator.validateCareerDates(baseDto);
        });
    }

    @Test
    public void testValidateCareerDates_fromAfterTo_shouldFail() {
        BaseCareerDtoWithDates baseDto = new BaseCareerDtoWithDates(
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(20)) {
        };
        Assertions.assertThrows(DataValidationException.class, () -> {
            CareerValidator.validateCareerDates(baseDto);
        });
    }

    @Test
    public void testValidateOwner_validOwner_shouldPass() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Career career = Mockito.mock(Career.class);
        Mockito.when(career.getUser()).thenReturn(user);

        CareerValidator.validateOwner(career, 1L);
    }

    @Test
    public void testValidateOwner_invalidOwner_shouldFail() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(1L);
        Career career = Mockito.mock(Career.class);
        Mockito.when(career.getUser()).thenReturn(user);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 2L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }

    @Test
    public void testValidateOwner_nullUser_shouldFail() {
        Career career = Mockito.mock(Career.class);
        Mockito.when(career.getUser()).thenReturn(null);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 1L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }

    @Test
    void validateOwner_userWithNullId_shouldFail() {
        User user = Mockito.mock(User.class);
        Mockito.when(user.getId()).thenReturn(null);
        Career career = Mockito.mock(Career.class);
        Mockito.when(career.getUser()).thenReturn(user);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> CareerValidator.validateOwner(career, 1L)
        );
        assertEquals("The user is not the owner of the career", ex.getMessage());
    }
}