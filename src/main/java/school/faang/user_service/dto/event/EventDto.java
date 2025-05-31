package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private int maxAttendees;
    private List<Long> attendeeIds;
    private List<Long> ratingIds;
    private Long ownerId;
    private List<Long> relatedSkillIds;
    private EventType type;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
