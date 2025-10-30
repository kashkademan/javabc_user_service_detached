package school.faang.user_service.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import school.faang.user_service.entity.promotion.Tarif;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class RedisPromotionEntity {

    private Long promotionId;
    private Long userId;
    private Tarif tarif;
    private Integer remainingImpressions;

}
