package school.faang.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventCreationNotAllowedException;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.util.ErrorResponseConstants;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = ex.getBindingResult();

        // Обработка ошибок валидации полей
        bindingResult.getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        // Обработка ошибок валидации объектов (например, @ValidEndDate)
        bindingResult.getGlobalErrors()
                .forEach(error -> errors.put(error.getObjectName(), error.getDefaultMessage()));

        // Формирование сообщения об ошибке для логирования
        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; ", "Ошибка валидации данных: ", ""));

        // Логирование ошибки с детальной информацией
        log.error("Validation error: {}", errorMessage);

        // Создание стандартного ответа об ошибке
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
        body.put(ErrorResponseConstants.STATUS, status.value());
        body.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.VALIDATION_ERROR);
        body.put(ErrorResponseConstants.MESSAGE, errorMessage);
        body.put(ErrorResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());
        body.put(ErrorResponseConstants.VALIDATION_ERRORS, errors);

        return new ResponseEntity<>(body, headers, status);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(
            DataValidationException ex,
            WebRequest request) {

        log.error("Data validation error: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
        body.put(ErrorResponseConstants.STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.VALIDATION_ERROR);
        body.put(ErrorResponseConstants.MESSAGE, ex.getMessage());
        body.put(ErrorResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());
        body.put(ErrorResponseConstants.VALIDATION_ERRORS, ex.getErrors());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(EventCreationNotAllowedException.class)
    public ResponseEntity<Object> handleEventCreationNotAllowedException(
            EventCreationNotAllowedException ex,
            WebRequest request) {

        log.error("Event creation not allowed: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
        body.put(ErrorResponseConstants.STATUS, HttpStatus.FORBIDDEN.value());
        body.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.FORBIDDEN_ERROR);
        body.put(ErrorResponseConstants.MESSAGE, ex.getMessage());
        body.put(ErrorResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<Object> handleRecordNotFoundException(
            RecordNotFoundException ex,
            WebRequest request) {

        log.error("Record not found: {}", ex.getMessage());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
        body.put(ErrorResponseConstants.STATUS, HttpStatus.NOT_FOUND.value());
        body.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.NOT_FOUND_ERROR);
        body.put(ErrorResponseConstants.MESSAGE, ex.getMessage());
        body.put(ErrorResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработчик для всех остальных исключений, которые не были обработаны специфическими обработчиками
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex,
            WebRequest request) {

        log.error("Unhandled exception occurred: ", ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(ErrorResponseConstants.TIMESTAMP, LocalDateTime.now());
        body.put(ErrorResponseConstants.STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(ErrorResponseConstants.ERROR, ErrorResponseConstants.INTERNAL_SERVER_ERROR);
        body.put(ErrorResponseConstants.MESSAGE, ErrorResponseConstants.INTERNAL_SERVER_ERROR_MESSAGE);
        body.put(ErrorResponseConstants.PATH, ((ServletWebRequest) request).getRequest().getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
