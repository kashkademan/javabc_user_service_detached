package school.faang.user_service.exception.handler;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventPublishingException;
import school.faang.user_service.exception.ForbiddenException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        return new ErrorResponse(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .toList()
        );
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationExceptions(DataValidationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenException(ForbiddenException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFeignException(FeignException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(EventPublishingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEventPublishingException(EventPublishingException ex) {
        return new ErrorResponse(ex.getMessage());
    }
}