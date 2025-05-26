package school.faang.user_service.dto;

import lombok.Data;

@Data
public class NotificationUserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;

    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
