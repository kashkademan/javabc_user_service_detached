package school.faang.user_service.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String country;
    private String city;
    private Integer minExperience;
}
