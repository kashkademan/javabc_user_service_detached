package school.faang.user_service.dto.event.response;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventResponse {
    private Long id;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private int maxAttendees;

    private Long ownerId;

    private EventType eventType;

    private EventStatus eventStatus;

    private List<Long> relatedSkills;
}
