package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;

import java.io.Serializable;
import java.time.LocalDateTime;

// TODO: наcтроить TTL
@RedisHash("promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRedisModel implements Serializable {
    @Id
    private String id;
    private Long userId;
    private Long eventId;
    private PromotionType type;
    private Long tariffId;
    private LocalDateTime endDate;
    private Integer countView;
    private PromotionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

