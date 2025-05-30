package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RedisHash("event_promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRedisModel implements Serializable {
    @Id
    private UUID id;
    @TimeToLive
    private Long ttl;
    private UUID promotionId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private int maxAttendees;
    private List<UUID> attendeeIds = new ArrayList<>();
    private List<UUID> ratingIds = new ArrayList<>();
    private Long ownerId;
    private List<UUID> relatedSkillIds = new ArrayList<>();
    private EventType type;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer coefficientPriority;
}
