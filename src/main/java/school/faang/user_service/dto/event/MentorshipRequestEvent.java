package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorshipRequestEvent {
    private Long followerId;
    private Long foloweeId;
    private long id;
}
