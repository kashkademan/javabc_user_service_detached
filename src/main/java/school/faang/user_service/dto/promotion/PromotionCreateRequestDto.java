package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionCreateRequestDto {
    private Long eventId;
//    private PromotionType type;
    private Long tariffId;
}
