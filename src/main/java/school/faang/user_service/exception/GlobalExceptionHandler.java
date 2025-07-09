package school.faang.user_service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler — централизованный обработчик исключений для REST-контроллеров.
 *
 * <p>Обрабатывает различные исключения, возникающие во время выполнения запросов, и возвращает
 * клиенту стандартизированные ответы с информацией об ошибках в формате JSON.
 * Это помогает унифицировать обработку ошибок и улучшить качество взаимодействия API с клиентами.</p>
 *
 * <p>В данном классе реализованы обработчики для:
 * <ul>
 *     <li>Некорректного формата JSON в запросах ({@link HttpMessageNotReadableException})</li>
 *     <li>Ошибок валидации данных (@Valid) ({@link MethodArgumentNotValidException})</li>
 *     <li>Ошибок бизнес-логики, например, при некорректной структуре временных интервалов
 *     ({@link DataValidationException})</li>
 *     <li>Случаев, когда ресурс не найден ({@link EntityNotFoundException})</li>
 *     <li>Ошибок авторизации, когда пользователь не предоставил заголовок
 *     {@code x-user-id} ({@link UnauthorizedException})</li>
 *     <li>И любых других необработанных исключений ({@link Exception})</li>
 * </ul>
 * </p>
 *
 * <p>Каждый обработчик возвращает объект {@link ErrorResponse} с HTTP-статусом, сообщением и отметкой времени.</p>
 *
 * @author agent
 * @since 05.07.2025
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                      WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid JSON format",
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage,
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(DataValidationException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                ex.getMessage(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}