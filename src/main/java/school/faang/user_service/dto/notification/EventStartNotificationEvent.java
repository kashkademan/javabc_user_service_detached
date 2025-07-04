package school.faang.user_service.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventStartNotificationEvent implements NotificationEvent {
    private UserDto owner;
    private List<UserDto> attendees;
    private String eventTitle;
}
