package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PremiumDto {
    private Long id;
    private Long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}