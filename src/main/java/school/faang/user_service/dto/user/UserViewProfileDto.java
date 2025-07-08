package school.faang.user_service.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import school.faang.user_service.entity.contact.PreferredContact;

@Getter
@Setter
@RequiredArgsConstructor
public class UserViewProfileDto {
    private long id;
    private String username;
    private String email;
    private String phone;
    private PreferredContact preference;
}
