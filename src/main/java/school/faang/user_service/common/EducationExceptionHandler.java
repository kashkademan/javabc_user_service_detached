package school.faang.user_service.common;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.controller.education.EducationController;

import java.time.LocalDateTime;

@ControllerAdvice(assignableTypes = EducationController.class)
@Slf4j
public class EducationExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleUserNotfoundException (EntityNotFoundException e){
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Не удалось найти сущность в базе",
                e.getMessage(),
                LocalDateTime.now()
        );
        log.error("Произошла ошибка");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
