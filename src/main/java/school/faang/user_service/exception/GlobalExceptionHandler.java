package school.faang.user_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException ex,
                                                                       HttpServletRequest request) {
        log.error("Data validation failed", ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), getFullPath(request));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handelForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        log.error("Forbidden action", ex);
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage(), getFullPath(request));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex,
                                                                       HttpServletRequest request) {
        log.error("Entity not found", ex);
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), getFullPath(request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        err -> Objects.requireNonNullElse(err.getDefaultMessage(), "")
                ));
        log.error("Validation failed for fields: {}", errors, ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", getFullPath(request));
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(), message, LocalDateTime.now(), path
        );
        return ResponseEntity.status(status).body(errorResponse);
    }

    private String getFullPath(HttpServletRequest request) {
        StringBuffer requestUrl = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString != null) {
            return requestUrl.append('?').append(queryString).toString();
        }
        return requestUrl.toString();
    }
}
