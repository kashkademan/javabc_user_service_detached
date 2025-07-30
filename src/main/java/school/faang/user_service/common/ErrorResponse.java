package school.faang.user_service.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String exceptionMessage;
    private LocalDateTime timestamp;
}
