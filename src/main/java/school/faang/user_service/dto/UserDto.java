package school.faang.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull(message = "ID must not be null")
    private Long id;
    @NotNull(message = "Name must not be null")
    private String username;
    @Email(message = "Only valid email address needed")
    private String email;
    @NotNull
    private List<UserDto> mentors;

    private PreferredContact preference;

    private String aboutMe;

    private Integer experience;

    private String phone;

    private String telegramUserName;
}


