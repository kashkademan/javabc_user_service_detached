package school.faang.user_service.dto.error;

import java.time.LocalDateTime;

public record ErrorResponse(String error,
                            String message,
                            LocalDateTime timestamp) {
}
