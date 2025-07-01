package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponseDto {
    private Long id;
    private Long userId;
    private Long eventId;
    private PromotionType type;
    private Long tariffId;
    private LocalDateTime endDate;
    private Integer countView;
    private PromotionStatus status;
}
