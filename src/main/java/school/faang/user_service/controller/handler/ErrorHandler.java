package school.faang.user_service.controller.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.ErrorResponseDto;
import school.faang.user_service.exception.UserServiceException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(UserServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleUserServiceException(UserServiceException e) {
        log.error("UserServiceException {}", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.FORBIDDEN.name(),
                "Action not permitted",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleIllegalArgument(IllegalArgumentException e) {
        log.error("IllegalArgumentException {}", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Invalid input data",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto illegalStateHandler(IllegalStateException e) {
        log.error("IllegalStateException {}", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.CONFLICT.name(),
                "Invalid operation state",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponseDto unsupportedOperationHandler(UnsupportedOperationException e) {
        log.error("UnsupportedOperationException {}", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.METHOD_NOT_ALLOWED.name(),
                "Operation not supported",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Exception with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something get wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}