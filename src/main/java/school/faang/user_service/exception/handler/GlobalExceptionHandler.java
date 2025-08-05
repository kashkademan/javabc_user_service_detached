package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.error.ErrorResponse;
import school.faang.user_service.exception.ActiveGoalsLimitExceededException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.GoalCompletedException;
import school.faang.user_service.exception.RejectMentorshipRequestByDateException;
import school.faang.user_service.exception.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Entity not found", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Data validation exception", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbiddenException(ForbiddenException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Forbidden", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ActiveGoalsLimitExceededException.class)
    public ErrorResponse handleActiveGoalsLimitExceededException(ActiveGoalsLimitExceededException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Active goals limit exceeded", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GoalCompletedException.class)
    public ErrorResponse handleGoalCompletedException(GoalCompletedException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Goal already completed", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(RejectMentorshipRequestByDateException.class)
    public ErrorResponse handleRejectMentorshipRequestByDateException(RejectMentorshipRequestByDateException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Request rejected by date expired", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("User not found", e.getMessage());
    }
}
