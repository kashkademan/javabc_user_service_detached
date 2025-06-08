package school.faang.user_service.model.redis.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash("promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRedisModel implements Serializable {
    private UUID key;
    private Long id;
    // TODO: нужно ли
    private Long userId;
    private Long eventId;
    private PromotionType type;
    private Long tariffId;
    private LocalDateTime endDate;
    private Integer countView;
    private PromotionStatus status;
    // TODO: нужно ли
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

