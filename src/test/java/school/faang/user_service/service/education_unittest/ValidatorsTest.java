package school.faang.user_service.service.education_unittest;

import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.user.Education;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static school.faang.user_service.service.education.Validators.validateUserIsEducationOwner;
import static school.faang.user_service.service.education.Validators.validateYearFrom;

class ValidatorsTest {

    @Test
    void validateYearFromShouldThrow_WhenYearTooHigh() {
        assertThatThrownBy(() -> validateYearFrom(2026))
                .isInstanceOf(DataValidationException.class);
    }

    @Test
    void validateYearFromShouldPass_WhenYearValid() {
        assertThatCode(() -> validateYearFrom(2000))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUserIsEducationOwnerShouldThrow_WhenNotOwner() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(2L);

        Education education = new Education();
        education.setUser(user2);

        assertThatThrownBy(() -> validateUserIsEducationOwner(1L, education))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Не достаточно прав");
    }

    @Test
    void validateUserIsEducationOwnerShouldPass_WhenOwner() {
        User user = new User();
        user.setId(1L);

        Education education = new Education();
        education.setUser(user);

        assertThatCode(() -> validateUserIsEducationOwner(1L, education))
                .doesNotThrowAnyException();
    }
}
