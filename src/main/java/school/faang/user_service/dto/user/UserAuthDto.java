package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.role.Role;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuthDto {
    private String username;
    private String password;
    private Set<Role> roles;
}
