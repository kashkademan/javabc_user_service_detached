package school.faang.user_service.exception.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventCreationNotAllowedException;
import school.faang.user_service.exception.PreConditionFailedException;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.exception.event.EventRegisterException;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.exception.work_schedule.WorkScheduleNotFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DataValidationException.class,
            EventRegisterException.class
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
            RecordNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PreConditionFailedException.class)
    public ResponseEntity<ErrorResponse> handlePreConditionFailedExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.PRECONDITION_FAILED.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(EventCreationNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenExceptions(RuntimeException e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                e.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
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
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
