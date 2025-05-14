package school.faang.user_service.dto.event;

import lombok.Data;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@Data
public class EventFilterDto {
    private String title;
    private Long ownerId;
    private String location;
    private EventType eventType;
    private EventStatus eventStatus;
}
