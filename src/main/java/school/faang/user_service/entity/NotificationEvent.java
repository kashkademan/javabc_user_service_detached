package school.faang.user_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.user.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {

    private UserDto userDto;
    private NotificationEventType type;
}