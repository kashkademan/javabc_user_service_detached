package school.faang.user_service.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SubscriptionErrorResponse;
import school.faang.user_service.exception.DataValidationException;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<SubscriptionErrorResponse> handleException(DataValidationException e) {
        SubscriptionErrorResponse response = new SubscriptionErrorResponse(e.getMessage(),
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
