package school.faang.user_service.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record UserUpdateDto(
        @NotBlank
        String username,
        @NotBlank
        String email,
        @NotBlank
        String phone,
        @NotBlank
        String aboutMe,
        @NonNull
        Long countryId,
        @NotBlank
        String city
) {
}
