package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowerEvent {
    Long followerId;
    Long followeeId;
}
