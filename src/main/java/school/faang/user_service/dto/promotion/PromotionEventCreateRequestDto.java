package school.faang.user_service.dto.promotion;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionEventCreateRequestDto {
    @NotNull(message = "EventId is mandatory")
    private Long eventId;
    @NotNull(message = "TariffId is mandatory")
    private Long tariffId;
}
