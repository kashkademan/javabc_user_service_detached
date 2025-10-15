package school.faang.user_service.service.education_unittest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.education.Validators;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ValidatorsTest {

    private Education education;

    private User user;

    @BeforeEach
    void setUp() {
        education = Mockito.mock(Education.class);
        user = Mockito.mock(User.class);
        when(education.getUser()).thenReturn(user);
    }

    @Test
    void validateYearFromYearToWhenYearFromInFutureThrowsException() {
        int futureYear = Year.now().getValue() + 1;
        assertThrows(DataValidationException.class,
                () -> Validators.validateYearFromYearTo(futureYear, null),
                "Должно выбрасываться исключение при годе начала в будущем");
    }

    @Test
    void validateYearFromYearToWhenYearToBeforeYearFromThrowsException() {
        assertThrows(DataValidationException.class,
                () -> Validators.validateYearFromYearTo(2020, 2019),
                "Должно выбрасываться исключение при некорректном диапазоне годов");
    }

    @Test
    void validateUserIsEducationOwnerWhenUserIsOwnerDoesNotThrow() {
        long ownerId = 1L;
        when(user.getId()).thenReturn(ownerId);

        assertDoesNotThrow(() ->
                        Validators.validateUserIsEducationOwner(ownerId, education),
                "Владелец должен проходить валидацию");
    }

    @Test
    void validateUserIsEducationOwnerWhenUserIsNullThrowsException() {
        when(education.getUser()).thenReturn(null);

        assertThrows(ForbiddenException.class,
                () -> Validators.validateUserIsEducationOwner(1L, education),
                "Должно выбрасываться исключение при отсутствии пользователя");
    }

    @Test
    void validateUserIsEducationOwnerWhenUserIsNotOwnerThrowsException() {
        when(user.getId()).thenReturn(2L);

        assertThrows(ForbiddenException.class,
                () -> Validators.validateUserIsEducationOwner(1L, education),
                "Должно выбрасываться исключение при несовпадении ID пользователя");
    }
}
