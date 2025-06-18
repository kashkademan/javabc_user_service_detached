package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequestDto {

    @NotBlank
    @Size(min = 3, max = 64, message = "Имя пользователя обязательно для заполнения")
    private String username;

    @Email(message = "Почта указано неверно")
    private String email;

    @NotBlank()
    private String password;

    @NotNull
    private Long countryId;
}