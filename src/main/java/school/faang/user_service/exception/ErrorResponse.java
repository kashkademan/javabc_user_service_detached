package school.faang.user_service.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
}
