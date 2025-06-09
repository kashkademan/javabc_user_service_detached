package school.faang.user_service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.dto.error.UserServiceErrorResponseDto;
import school.faang.user_service.exception.file.FileUploadException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class FileExceptionHandler {
    private static final Map<Class<? extends Exception>, HttpStatus> httpStatusMap = Map.of(
            FileUploadException.class, HttpStatus.SERVICE_UNAVAILABLE
    );

    @ExceptionHandler({
            FileUploadException.class
    })
    public ResponseEntity<UserServiceErrorResponseDto> handleException(Exception ex) {
        String errorMessage = ex.getMessage();
        HttpStatus status = getHttpStatus(ex);

        return createErrorResponse(errorMessage, status, ex);
    }

    private HttpStatus getHttpStatus(Throwable ex) {
        return httpStatusMap.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<UserServiceErrorResponseDto> createErrorResponse(String errorMsg,
                                                                            HttpStatus status,
                                                                            Exception ex) {
        log.error("Error in user-service: {}, response status {}", errorMsg, status, ex);
        UserServiceErrorResponseDto response =
                new UserServiceErrorResponseDto(errorMsg, LocalDateTime.now(), status.value());
        return ResponseEntity.status(status).body(response);
    }
}
