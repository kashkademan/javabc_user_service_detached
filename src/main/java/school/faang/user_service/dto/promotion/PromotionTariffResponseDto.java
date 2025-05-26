package school.faang.user_service.dto.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionTariffResponseDto {
    private Long id;
    private BigDecimal price;
    private Integer countView;
    private Integer durationDays;
}
