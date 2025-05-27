package school.faang.user_service.exception;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import school.faang.user_service.dto.error.ErrorResponse;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UserNotFoundException.class,
            AmazonS3Exception.class,
            EntityNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusNotFound(Exception ex) {
        return ResponseEntity.status(NOT_FOUND).body(getErrorResponse(ex));
    }

    @ExceptionHandler({
            FileSizeException.class,
            InvalidImageFormatException.class,
            MultipartException.class,
            ConvertingDataException.class,
            JsonSerializationException.class,
            JsonDeserializationException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusBadRequest(Exception ex) {
        return ResponseEntity.status(BAD_REQUEST).body(getErrorResponse(ex));
    }

    private ErrorResponse getErrorResponse(Exception ex) {
        log.error("{}", ex.toString());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .build();
    }
}
