package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.unprocessableEntity().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseError(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getCause();

        if (rootCause instanceof InvalidFormatException invalid) {
            String field = extractField(invalid.getPath());
            //String expectedType = invalid.getTargetType().getSimpleName();
            //String message = "Invalid format for field '" + field + "'. Expected type: " + expectedType;
            String message = "Invalid format for field '" + field;

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", message));
        }

        return ResponseEntity
                .badRequest()
                .body(Map.of("error", rootCause.getMessage()));
    }

    private static String extractField(List<JsonMappingException.Reference> path) {
        return (path == null || path.isEmpty())
                ? "unknown field"
                : path.get(path.size() - 1).getFieldName();
    }
}

