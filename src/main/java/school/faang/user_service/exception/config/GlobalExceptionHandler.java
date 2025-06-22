package school.faang.user_service.exception.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import school.faang.user_service.exception.common.DataValidationException;
import school.faang.user_service.exception.common.FileException;
import school.faang.user_service.exception.event.EventCreationNotAllowedException;
import school.faang.user_service.exception.common.PreConditionFailedException;
import school.faang.user_service.exception.common.RecordNotFoundException;
import school.faang.user_service.exception.event.EventRegisterException;
import school.faang.user_service.exception.goal.GoalNotExistException;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;
import school.faang.user_service.exception.goal.UpdateComleteGoalException;
import school.faang.user_service.exception.goal.UpdateGoalWithActiveSubGoalsException;
import school.faang.user_service.exception.goal.UserNotGoalOwnerException;
import school.faang.user_service.exception.payment.PaymentException;
import school.faang.user_service.exception.skill.SkillNotExistException;
import school.faang.user_service.exception.users.UserIdNotFoundException;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.exception.work_schedule.WorkScheduleNotFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DataValidationException.class,
            EventRegisterException.class,
            FileException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            WorkScheduleNotFoundException.class,
            RecordNotFoundException.class,
            GoalNotExistException.class,
            SkillNotExistException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            PreConditionFailedException.class,
            MaxActiveGoalPerUserException.class,
            UpdateComleteGoalException.class,
            UpdateGoalWithActiveSubGoalsException.class
    })
    public ResponseEntity<ErrorResponse> handlePreConditionFailedExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.PRECONDITION_FAILED.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler({
            EventCreationNotAllowedException.class,
            UserNotGoalOwnerException.class
    })
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation error - " + errors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "The request body is missing or contains invalid JSON"
        );
        log.error("handleHttpMessageNotReadable", e);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.PAYMENT_REQUIRED.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        log.error("handleException", e);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}