package school.faang.user_service.dto.premium;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class PremiumDto {
    private long id;
    private long userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
