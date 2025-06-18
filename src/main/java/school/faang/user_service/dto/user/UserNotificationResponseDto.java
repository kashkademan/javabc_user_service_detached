package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationResponseDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
}
