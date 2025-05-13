package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalErrorResponseDto {
    private String errorMsg;
    private LocalDateTime timestamp;
    private int codeResponse;
}
