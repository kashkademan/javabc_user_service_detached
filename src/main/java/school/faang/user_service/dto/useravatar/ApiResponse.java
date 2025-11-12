package school.faang.user_service.dto.useravatar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;
    private LocalDateTime timestamp = LocalDateTime.now();
}
