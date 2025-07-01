package school.faang.user_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterResponseDto {
    private long id;
    private String username;
}