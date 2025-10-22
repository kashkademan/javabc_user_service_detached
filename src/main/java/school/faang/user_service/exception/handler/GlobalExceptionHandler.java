package school.faang.user_service.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всех REST-контроллеров.
 * Перехватывает внутренние исключения и возвращает единый JSON-ответ клиенту.
 * Позволяет централизованно управлять кодами ошибок и логикой отображения.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обработка ошибок валидации (аннотации @Valid, @NotBlank, @Email и т.д.).
     * Spring автоматически выбрасывает MethodArgumentNotValidException,
     * если DTO не проходит проверку Bean Validation.
     * Пример ответа:
     * {
     *   "status": 400,
     *   "error": "Validation failed",
     *   "fields": { "email": "must be a valid email" }
     * }
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
            fieldErrors.put(fieldName, error.getDefaultMessage());
        });

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation failed");
        response.put("fields", fieldErrors);

        log.warn("Validation error: {}", fieldErrors);
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Обработка бизнес-ошибок, связанных с неверными данными.
     * Например: неправильная длина пароля, дублирующийся email и т.п.
     *
     * Исключение выбрасывается вручную как DataValidationException.
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDataValidation(DataValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Invalid request data");
        body.put("message", ex.getMessage());

        log.warn("Business validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Обработка ошибок доступа — когда пользователь пытается изменить чужой профиль.
     * Возвращает 403 Forbidden.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Access denied");
        body.put("message", ex.getMessage());

        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Обработка ошибок, связанных с отсутствием данных.
     * Например: "User not found", "Country not found" и т.д.
     * Возвращает 404 Not Found, если в сообщении содержится "not found",
     * иначе 400 Bad Request для других RuntimeException.
     */
    @ExceptionHandler({ RuntimeException.class, IllegalArgumentException.class })
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        HttpStatus status = ex.getMessage() != null && ex.getMessage().toLowerCase().contains("not found")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status == HttpStatus.NOT_FOUND ? "Not Found" : "Bad Request");
        body.put("message", ex.getMessage());

        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Обработка всех остальных неожиданных исключений.
     * Используется как "страховка" на случай ошибок, не предусмотренных выше.
     * Возвращает 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal server error");
        body.put("message", ex.getMessage());

        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}