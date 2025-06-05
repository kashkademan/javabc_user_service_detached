package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponseDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String aboutMe;
    private String city;
    private Integer experience;
    // TODO: fileId
}
