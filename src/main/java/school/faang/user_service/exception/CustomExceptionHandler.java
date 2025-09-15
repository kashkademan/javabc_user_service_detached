package school.faang.user_service.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<String> dataNotFound(DataNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<String> handleFileTooLargeException(
            FileTooLargeException e, WebRequest request) {
        log.warn("File too large: {}", e.getMessage(), e);
        return new ResponseEntity<>("File error: " + e.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(AvatarProcessingException.class)
    public ResponseEntity<String> handleAvatarProcessingException(
            AvatarProcessingException e, WebRequest request) {
        log.error("Failed to process avatar: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "Error processing avatar: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AvatarNotFoundException.class)
    public ResponseEntity<String> handleAvatarNotFoundException(
            AvatarNotFoundException e, WebRequest request) {
        log.warn("Avatar not found: {}", e.getMessage(), e);
        return new ResponseEntity<>("Avatar not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<String> handleInvalidFileTypeException(
            InvalidFileTypeException e, WebRequest request) {
        log.warn("Invalid file type: {}", e.getMessage(), e);
        return new ResponseEntity<>("Invalid file type: " + e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            UserNotFoundException e, WebRequest request) {
        log.warn("User not found: {}", e.getMessage(), e);
        return new ResponseEntity<>("User not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e, WebRequest request) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "Unexpected error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EventPublishException.class)
    public ResponseEntity<String> handleEventPublishException(EventPublishException e, WebRequest request) {
        log.error("Failed to publish goal completion event: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "Failed to complete goal due to event publish error: " + e.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler(GoalNotFoundException.class)
    public ResponseEntity<String> handleGoalNotFoundException(
            GoalNotFoundException e, WebRequest request) {
        log.warn("Goal not found: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "Goal not found: " + e.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(GoalNotAssignedToUserException.class)
    public ResponseEntity<String> handleGoalNotAssignedToUserException(
            GoalNotAssignedToUserException e, WebRequest request) {
        log.warn("Goal not assigned to user: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                "Goal not assigned to user: " + e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(GoalAlreadyCompletedException.class)
    public ResponseEntity<String> handleGoalAlreadyCompletedException(
            GoalAlreadyCompletedException e, WebRequest request) {
        log.warn("Goal already completed: {}", e.getMessage(), e);
        return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(
            DataValidationException e, WebRequest request) {
        log.warn("Data validation error: {}", e.getMessage(), e);
        return new ResponseEntity<>("Validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
