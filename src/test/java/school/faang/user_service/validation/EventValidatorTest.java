package school.faang.user_service.validation;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static school.faang.user_service.preparation.test.PreparationTest.CURRENT_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_1;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_ID;
import static school.faang.user_service.preparation.test.PreparationTest.MINUS_DAYS_1;
import static school.faang.user_service.preparation.test.PreparationTest.NOW;
import static school.faang.user_service.preparation.test.PreparationTest.OTHER_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OWNER_1;
import static school.faang.user_service.preparation.test.PreparationTest.PLUS_DAYS_1;
import static school.faang.user_service.preparation.test.PreparationTest.PLUS_DAYS_2;


@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private EventValidator eventValidator;

    @Test
    void validateEventNotInPast_ThrowsExceptionWhenStartDateInPast() {
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventNotInPast(MINUS_DAYS_1));
    }

    @Test
    void validateEventDates_ThrowsExceptionWhenEndDateBeforeStartDate() {
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventDates(PLUS_DAYS_2, PLUS_DAYS_1));
    }

    @Test
    void validateAndGetUser_ThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> eventValidator.validateAndGetUser(CURRENT_USER_ID));
    }

    @Test
    void validateEventOwnership_ThrowsExceptionWhenUserNotOwner() {
        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
        assertThrows(ForbiddenException.class,
                () -> eventValidator.validateEventOwnership(EVENT_1));
    }

    @Test
    void validateEventOwnership_ThrowsExceptionWhenEventOwnerIsNull() {
        Event event = Event.builder().id(EVENT_ID).owner(null).build();
        assertThrows(NullPointerException.class,
                () -> eventValidator.validateEventOwnership(event));
    }

    @Test
    void validateEventNotInPast_ThrowsExceptionWhenStartDateIsNow() {
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventNotInPast(NOW));
    }

    @Test
    void validateEventDates_ThrowsExceptionWhenEndDateEqualsStartDate() {
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventDates(PLUS_DAYS_1, PLUS_DAYS_1));
    }

    @Test
    void validateEventNotInPast_DoesNotThrowWhenStartDateInFuture() {
        assertDoesNotThrow(() -> eventValidator.validateEventNotInPast(PLUS_DAYS_1));
    }

    @Test
    void validateEventDates_DoesNotThrowWhenEndDateAfterStartDate() {
        assertDoesNotThrow(() -> eventValidator.validateEventDates(PLUS_DAYS_1, PLUS_DAYS_2));
    }

    @Test
    void validateAndGetUser_ReturnsUserWhenUserExists() {
        User expectedUser = OWNER_1;

        when(userRepository.findById(CURRENT_USER_ID)).thenReturn(Optional.of(expectedUser));
        User result = eventValidator.validateAndGetUser(CURRENT_USER_ID);

        assertEquals(expectedUser, result);
    }

    @Test
    void validateEventOwnership_DoesNotThrowWhenUserIsOwner() {
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);

        assertDoesNotThrow(() -> eventValidator.validateEventOwnership(EVENT_1));
    }
}
