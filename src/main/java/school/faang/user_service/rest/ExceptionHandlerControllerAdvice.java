package school.faang.user_service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.dto.mentorship.ErrorResponseDto;
import school.faang.user_service.exception.ContactNotFoundException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ErrorResponse;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.UnauthorizedException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestValidationException;
import school.faang.user_service.utils.Utils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerControllerAdvice {
    public static final String RUNTIME_ERROR = "Runtime error, see log";
    private final Utils utils;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDto> handlerResponseStatusException(ResponseStatusException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new ErrorResponseDto(e.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> result = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
        log.error("handlerMethodArgumentNotValidException: {}", exception.getMessage(), exception);
        return result;
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            RecommendationRequestNotFoundException.class,
            EventNotFoundException.class,
            ContactNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(RuntimeException e) {
        return getErrorResponse("handlerNotFoundException", e);
    }

    @ExceptionHandler(RecommendationRequestException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerRecommendationRequestException(RecommendationRequestException e) {
        return getErrorResponse("handleRecommendationRequestException", e);
    }

    @ExceptionHandler({RecommendationRequestValidationException.class, DataValidationException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerValidationException(RuntimeException e) {
        return getErrorResponse("handlerValidationException", e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlerUnauthorizedException(UnauthorizedException e) {
        return getErrorResponse("handlerUnauthorizedException", e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handlerRuntimeException(RuntimeException e) {
        return getErrorResponse("handlerRuntimeException", RUNTIME_ERROR, e);
    }

    private ErrorResponse getErrorResponse(String exceptionLabel, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    private ErrorResponse getErrorResponse(String exceptionLabel, String errorMessage, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(errorMessage);
    }
}