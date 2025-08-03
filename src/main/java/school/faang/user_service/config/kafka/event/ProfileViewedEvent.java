package school.faang.user_service.config.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
public class ProfileViewedEvent {
    private String viewerName;
    private String userName;
    private Long viewerId;
    private Long viewedId;
    private LocalDateTime localDateTime;
}
