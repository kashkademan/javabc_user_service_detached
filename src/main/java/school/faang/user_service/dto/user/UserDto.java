package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class UserDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private Long telegramId;
    private PreferredContact preference;
    private List<Long> mentorIds;
    private List<Long> menteeIds;

    public UserDto(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}