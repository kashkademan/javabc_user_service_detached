package school.faang.user_service.model.redis.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import school.faang.user_service.entity.promotion.PromotionStatus;
import school.faang.user_service.entity.promotion.PromotionType;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRedisModel implements Serializable {
    @Id
    private String key;
    private Long id;
    @Indexed
    private Long userId;
    @Indexed
    private Long eventId;
    private PromotionType type;
    private Long tariffId;
    private LocalDateTime endDate;
    private Integer countView;
    private PromotionStatus status;
    private LocalDateTime createdAt;
}

