package school.faang.user_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.goal.GoalErrorResponseDto;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.exception.skill.SkillNotFoundException;
import school.faang.user_service.exception.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class UserServiceExceptionHandler {
    private static final Map<Class<? extends Throwable>, HttpStatus> httpStatusMap = Map.of(
            GoalNotFoundException.class, HttpStatus.NOT_FOUND,
            UserNotFoundException.class, HttpStatus.NOT_FOUND,
            SkillNotFoundException.class, HttpStatus.NOT_FOUND,
            CountActiveGoalMoreMaxException.class, HttpStatus.CONFLICT,
            GoalAlreadyCompletedException.class, HttpStatus.CONFLICT,
            MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST,
            Exception.class, HttpStatus.INTERNAL_SERVER_ERROR
    );
    private static final Map<Class<? extends Throwable>, ErrorHandler> errorHandlers = Map.of(
            MethodArgumentNotValidException.class, ex ->
                    ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().stream()
                            .map(error -> String.format("Field '%s' %s",
                                    ((FieldError) error).getField(), error.getDefaultMessage()))
                            .collect(Collectors.joining(", "))
    );

    @ExceptionHandler({
            GoalNotFoundException.class,
            UserNotFoundException.class,
            SkillNotFoundException.class,
            CountActiveGoalMoreMaxException.class,
            GoalAlreadyCompletedException.class,
            MethodArgumentNotValidException.class,
            Exception.class
    })
    public ResponseEntity<GoalErrorResponseDto> handleException(Exception ex) {
        ErrorHandler handler = getErrorHandler(ex);
        String errorMessage = handler.handle(ex);
        HttpStatus status = getHttpStatus(ex);

        return createErrorResponse(errorMessage, status);
    }

    private HttpStatus getHttpStatus(Throwable ex) {
        return httpStatusMap.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(ex.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorHandler getErrorHandler(Throwable ex) {
        return errorHandlers.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(ex.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Throwable::getMessage);
    }

    private ResponseEntity<GoalErrorResponseDto> createErrorResponse(String errorMsg, HttpStatus status) {
        log.error("Error in GoalController: {}, response status {}", errorMsg, status);
        GoalErrorResponseDto response = new GoalErrorResponseDto(errorMsg, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(response, status);
    }
}
