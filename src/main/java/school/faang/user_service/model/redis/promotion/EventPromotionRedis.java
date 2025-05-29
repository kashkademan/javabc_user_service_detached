package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;

import java.time.LocalDateTime;

@RedisHash("event_promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPromotionRedis {
    @Id
    private String id;
    private EventRedis event;
    private PromotionType type;
    private PromotionTariffRedis tariff;
    private LocalDateTime endDate;
    private Integer countView;
    private PromotionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

