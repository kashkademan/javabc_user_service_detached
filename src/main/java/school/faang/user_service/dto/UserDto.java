package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    @NotNull(message ="ID must not be null")
    private Long id;

    @NotNull (message ="Name must not be null")
    private String username;

    @Email(message ="Only valid email address needed")
    private String email;

    @NotNull
    private List<UserDto> mentors;
}