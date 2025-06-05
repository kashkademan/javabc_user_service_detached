package school.faang.user_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 64, message = "Username must be between 3 and 64 characters")
    private String username;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Size(max = 64)
    private String email;
    @NotBlank(message = "Phone is mandatory")
    @Size(max = 64)
    private String phone;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 128, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 4096, message = "About me must be at most 4096 characters")
    private String aboutMe;

    @Size(max = 64, message = "City name must be at most 64 characters")
    private String city;

    @Min(value = 0, message = "Experience must be zero or positive")
    private Integer experience;
}
