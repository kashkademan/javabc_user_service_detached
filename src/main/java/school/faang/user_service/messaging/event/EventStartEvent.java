package school.faang.user_service.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStartEvent implements Serializable {
    private String eventId;
    private List<Long> participanIds;
}
