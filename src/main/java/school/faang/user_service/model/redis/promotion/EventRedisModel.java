package school.faang.user_service.model.redis.promotion;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RedisHash("event")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRedisModel implements Serializable {
    @Id
    private String id;
    private String promotionId;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private int maxAttendees;
    private List<Long> attendeeIds = new ArrayList<>();
    private List<Long> ratingIds = new ArrayList<>();
    private Long ownerId;
    private List<Long> relatedSkillIds = new ArrayList<>();
    private EventType type;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // TODO: поле не смаплено и дублируется
    private Integer coefficientPriority;
}
