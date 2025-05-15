package school.faang.user_service.dto.event.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private Long ownerId;
}
