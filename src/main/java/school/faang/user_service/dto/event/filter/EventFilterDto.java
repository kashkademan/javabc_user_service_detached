package school.faang.user_service.dto.event.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private Long ownerId;
}
