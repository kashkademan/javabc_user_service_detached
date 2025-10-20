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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static school.faang.user_service.preparation.test.PreparationTest.CURRENT_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.EVENT_ID;
import static school.faang.user_service.preparation.test.PreparationTest.OTHER_USER_ID;
import static school.faang.user_service.preparation.test.PreparationTest.createEvent;
import static school.faang.user_service.preparation.test.PreparationTest.createUser;


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
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventNotInPast(pastDate));
    }

    @Test
    void validateEventDates_ThrowsExceptionWhenEndDateBeforeStartDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(2);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventDates(startDate, endDate));
    }

    @Test
    void validateAndGetUser_ThrowsExceptionWhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> eventValidator.validateAndGetUser(1L));
    }

    @Test
    void validateEventOwnership_ThrowsExceptionWhenUserNotOwner() {
        Event event = createEvent(EVENT_ID, createUser(CURRENT_USER_ID));

        when(userContext.getUserId()).thenReturn(OTHER_USER_ID);
        assertThrows(ForbiddenException.class,
                () -> eventValidator.validateEventOwnership(event));
    }

    @Test
    void validateEventOwnership_ThrowsExceptionWhenEventOwnerIsNull() {
        Event event = Event.builder().id(EVENT_ID).owner(null).build();
        assertThrows(NullPointerException.class,
                () -> eventValidator.validateEventOwnership(event));
    }

    @Test
    void validateEventNotInPast_ThrowsExceptionWhenStartDateIsNow() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventNotInPast(now));
    }

    @Test
    void validateEventDates_ThrowsExceptionWhenEndDateEqualsStartDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        assertThrows(ValidationException.class,
                () -> eventValidator.validateEventDates(startDate, startDate));
    }

    @Test
    void validateEventNotInPast_DoesNotThrowWhenStartDateInFuture() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        assertDoesNotThrow(() -> eventValidator.validateEventNotInPast(futureDate));
    }

    @Test
    void validateEventDates_DoesNotThrowWhenEndDateAfterStartDate() {
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(2);
        assertDoesNotThrow(() -> eventValidator.validateEventDates(startDate, endDate));
    }

    @Test
    void validateAndGetUser_ReturnsUserWhenUserExists() {
        User expectedUser = createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        User result = eventValidator.validateAndGetUser(1L);

        assertEquals(expectedUser, result);
    }

    @Test
    void validateEventOwnership_DoesNotThrowWhenUserIsOwner() {
        Event event = createEvent(EVENT_ID, createUser(CURRENT_USER_ID));
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);

        assertDoesNotThrow(() -> eventValidator.validateEventOwnership(event));
    }
}
