package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDto(
        @NotBlank(message = "Name should be present!")
        String username,
        @NotBlank(message = "Email should be present!")
        String email,
        String phone,
        String aboutMe,
        @NotNull(message = "Country id should be present!")
        Long countryId,
        String city
) {
}
