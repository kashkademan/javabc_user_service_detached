package school.faang.user_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilter {
    private String title;
    private EventType eventType;
    private EventStatus eventStatus;
    private LocalDateTime startFrom;
    private LocalDateTime startTo;
}
