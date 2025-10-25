package school.faang.user_service.dto.mentorship;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime localDateTime;
}
